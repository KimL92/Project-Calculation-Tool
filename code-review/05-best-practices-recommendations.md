# Best Practices & Recommendations

## 1. Use Spring Data JPA Instead of JDBC

### Current Approach
- Manual SQL queries with JdbcTemplate
- Manual row mapping
- No relationship management

### Recommended Approach
```java
// Use JPA entities
@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee creator;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<SubProject> subProjects;

    // Automatic duration calculation
    @Transient
    public int getProjectDuration() {
        if (projectStartDate != null && projectDeadline != null) {
            return (int) ChronoUnit.DAYS.between(projectStartDate, projectDeadline);
        }
        return 0;
    }
}

// Repository becomes simple
public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCreatorEmployeeId(int employeeId);
}
```

**Benefits**:
- Less boilerplate code
- Automatic relationship management
- Type-safe queries
- Built-in pagination and sorting

## 2. Implement Bean Validation

### Current Approach
```java
// Manual validation scattered in controllers
if (year < 2000 || year > 2100) {
    model.addAttribute("error", "Start date year must be between 2000 and 2100");
    return "createproject";
}
```

### Recommended Approach
```java
// In model class
public class Project {
    @NotNull
    @Size(min = 1, max = 255)
    private String projectName;

    @PastOrPresent
    @YearRange(min = 2000, max = 2100)
    private LocalDate projectStartDate;

    @Future
    @DateAfter("projectStartDate")
    private LocalDate projectDeadline;
}

// In controller
@PostMapping("/create/{employeeId}")
public String createProject(@PathVariable int employeeId,
                          @Valid @ModelAttribute Project project,
                          BindingResult bindingResult,
                          Model model) {
    if (bindingResult.hasErrors()) {
        return "createproject";
    }
    // Process valid project
}
```

## 3. Extract Validation Logic

### Create Validation Service
```java
@Service
public class ValidationService {
    private static final int MIN_YEAR = 2000;
    private static final int MAX_YEAR = 2100;

    public ValidationResult validateDateRange(LocalDate startDate, LocalDate endDate) {
        ValidationResult result = new ValidationResult();

        if (startDate != null && startDate.getYear() < MIN_YEAR || startDate.getYear() > MAX_YEAR) {
            result.addError("Start date year must be between " + MIN_YEAR + " and " + MAX_YEAR);
        }

        if (endDate != null && endDate.getYear() < MIN_YEAR || endDate.getYear() > MAX_YEAR) {
            result.addError("Deadline year must be between " + MIN_YEAR + " and " + MAX_YEAR);
        }

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            result.addError("Deadline cannot be before start date");
        }

        return result;
    }

    public ValidationResult validateDateWithinParent(LocalDate date, LocalDate parentStart, LocalDate parentEnd) {
        ValidationResult result = new ValidationResult();

        if (date != null && parentStart != null && date.isBefore(parentStart)) {
            result.addError("Date must be within parent period");
        }

        if (date != null && parentEnd != null && date.isAfter(parentEnd)) {
            result.addError("Date must be within parent period");
        }

        return result;
    }
}
```

## 4. Use DTOs for Data Transfer

### Current Approach
- Model classes used directly in controllers
- Exposes internal structure
- No separation of concerns

### Recommended Approach
```java
// DTO for creating project
public class CreateProjectDTO {
    @NotBlank
    @Size(max = 255)
    private String projectName;

    @Size(max = 1000)
    private String projectDescription;

    @NotNull
    private LocalDate projectStartDate;

    @NotNull
    private LocalDate projectDeadline;

    @NotBlank
    private String projectCustomer;
}

// Controller uses DTO
@PostMapping("/create/{employeeId}")
public String createProject(@PathVariable int employeeId,
                          @Valid @ModelAttribute CreateProjectDTO dto,
                          BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
        return "createproject";
    }

    Project project = projectService.createProjectFromDTO(dto, employeeId);
    // ...
}
```

## 5. Implement Proper Logging

### Current Approach
```java
System.out.println("DEBUG - SubProjectId: " + subProjectId);
e.printStackTrace();
```

### Recommended Approach
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ProjectService {
    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    public void createProject(...) {
        logger.debug("Creating project: {}", projectName);
        try {
            // ...
            logger.info("Project created successfully: {}", projectId);
        } catch (Exception e) {
            logger.error("Failed to create project: {}", projectName, e);
            throw e;
        }
    }
}
```

## 6. Use Constants for Magic Values

### Current Approach
```java
if (year < 2000 || year > 2100) {
    // ...
}
```

### Recommended Approach
```java
public class DateConstants {
    public static final int MIN_YEAR = 2000;
    public static final int MAX_YEAR = 2100;

    private DateConstants() {
        // Utility class
    }
}

// Or use @ConfigurationProperties
@ConfigurationProperties(prefix = "app.dates")
public class DateConfig {
    private int minYear = 2000;
    private int maxYear = 2100;
    // getters/setters
}
```

## 7. Implement Transaction Management

### Current Approach
- No explicit transaction boundaries
- Potential for partial updates

### Recommended Approach
```java
@Service
@Transactional
public class ProjectService {

    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(long projectId) {
        // All operations in transaction
        taskRepository.deleteTasksByProject(projectId);
        subProjectRepository.deleteSubProjectsByProject(projectId);
        projectRepository.deleteProject(projectId);
        // If any fails, all rollback
    }
}
```

## 8. Use Exception Handling

### Current Approach
```java
catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
    return false;
}
```

### Recommended Approach
```java
// Custom exceptions
public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(Long projectId) {
        super("Project not found: " + projectId);
    }
}

public class ValidationException extends RuntimeException {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super("Validation failed");
        this.errors = errors;
    }
}

// Global exception handler
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    public String handleProjectNotFound(ProjectNotFoundException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidation(ValidationException e, Model model) {
        model.addAttribute("errors", e.getErrors());
        return "validation-error";
    }
}
```

## 9. Extract Common Controller Logic

### Current Approach
- Repeated employee header setup
- Repeated validation patterns

### Recommended Approach
```java
// Base controller or utility
public class ControllerUtils {
    public static void addEmployeeHeader(Model model, EmployeeService employeeService, int employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }
    }
}

// Or use @ModelAttribute
@Controller
public class ProjectController {

    @ModelAttribute("currentEmployee")
    public Employee getCurrentEmployee(@PathVariable(required = false) Integer employeeId) {
        if (employeeId != null) {
            return employeeService.getEmployeeById(employeeId);
        }
        return null;
    }
}
```

## 10. Use Configuration Properties

### Current Approach
- Hardcoded values
- No external configuration

### Recommended Approach
```java
// application.properties
app.dates.min-year=2000
app.dates.max-year=2100
app.security.password.min-length=8
app.security.session.timeout=1800

// Configuration class
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private Dates dates = new Dates();
    private Security security = new Security();

    // getters/setters
}
```

## 11. Manage Secrets and Runtime Profiles

### Current Approach
- Secrets are committed in config (e.g., DB password in `src/main/resources/application-mysql.properties`)
- `src/main/resources/application.properties` forces `spring.profiles.active=mysql` for all runs

### Recommended Approach
- Never commit credentials; use environment variables / secret store
- Do not hardcode `spring.profiles.active` in the repo; set it per environment (`SPRING_PROFILES_ACTIVE`)
- Prefer profile-specific files that reference env vars:
```properties
# application-mysql.properties (safe version)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

## 12. Implement Caching

### Recommended
```java
@Service
public class ProjectService {

    @Cacheable(value = "projects", key = "#projectId")
    public Project getProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    @CacheEvict(value = "projects", key = "#project.projectID")
    public void updateProject(Project project) {
        projectRepository.editProject(project);
    }
}
```

## 13. Add API Documentation

### Recommended
- Use Swagger/OpenAPI for REST endpoints
- Add JavaDoc to all public methods
- Document business rules

## Priority Recommendations

### High Priority
1. ✅ Implement password hashing
2. ✅ Add input validation (Bean Validation)
3. ✅ Extract validation logic
4. ✅ Implement proper logging
5. ✅ Add transaction management
6. ✅ Remove committed secrets + use env/secret store

### Medium Priority
1. Consider migrating to JPA
2. Implement exception handling
3. Extract common controller logic
4. Use configuration properties
5. Add constants for magic values

### Low Priority
1. Implement caching
2. Add API documentation
3. Use DTOs (if building REST API)
