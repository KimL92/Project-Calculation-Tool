# Technical Debt & TODOs

## Existing TODOs in Code

### 1. ProjectController.java

#### Line 164: Unused Method
```java
// TODO: Vurder om denne kan slettes (kan nok godt)
@PostMapping("/create/{employeeId}")
public String createProject(...)
```
**Status**: Method exists but may be unused
**Action**: Verify if method is used, remove if not needed

#### Line 257-258: SubProject Delete
```java
// TODO DELETE TIL SUBPROJECT - kig på den Aden har lavet den
// TODO: KIG OGSÅ PÅ LINJE 123 EFTER PROJECTID
@PostMapping("/subproject/delete/{employeeId}/{projectId}/{subProjectId}")
```
**Status**: Delete functionality exists but needs review
**Action**: Review implementation, ensure proper cascade handling

### 2. Commented Code

#### ProjectController.java (Lines 24-33)
```java
/* VI BRUGER DEN IKKE
// her laver vi metoderene på hvad de forskellig bruger skal kunne.
public boolean isManager(Employee employee){
    return employee != null && employee.getRole() == EmployeeRole.PROJECT_MANAGER;
}
```
**Status**: Commented out code
**Action**: Remove if not needed, or document why kept

#### ProjectController.java (Line 207)
```java
//        projectService.assignEmployeeToProject(selectedEmployeeId, project.getProjectID());
```
**Status**: Commented code
**Action**: Remove or implement

#### ProjectRepository.java (Lines 112-116)
```java
/*
public void deleteProject(long projectID) {
    jdbcTemplate.update("DELETE FROM project WHERE project_id = ?", projectID);
}
*/
```
**Status**: Old implementation replaced
**Action**: Remove commented code

#### TaskController.java (Lines 523-535)
```java
//    @PostMapping("/subtask/save/{employeeId}/{projectId}/{subProjectId}/{taskId}/{subTaskId}")
//    public String saveSubTask(...)
```
**Status**: Commented endpoint
**Action**: Remove or implement

### 3. Debug Code

#### TaskController.java (Line 138)
```java
// Debug - fjern denne linje efter test
System.out.println("DEBUG - SubProjectId: " + subProjectId + ", SubProject: " + subProject);
```
**Status**: Debug statement left in code
**Action**: Remove immediately

#### TaskController.java (Lines 417-424)
```java
// TILFØJ DENNE TRY-CATCH
try {
    Task parentTask = taskService.getTaskById(taskId);
    if (parentTask == null) {
        System.out.println("ERROR - Task findes ikke: taskId=" + taskId);
        // ...
    }
} catch (Exception e) {
    System.out.println("ERROR - Task findes ikke i databasen: taskId=" + taskId);
    System.out.println("Tjek din task.html - linket sender forkert taskId!");
    // ...
}
```
**Status**: Debug/error handling with System.out.println
**Action**: Replace with proper logging

## Technical Debt Items

### 1. Code Duplication

#### Date Validation
**Location**: Multiple controllers
**Issue**: Same validation logic repeated
**Files**:
- `ProjectController.createProject()` (lines 172-188)
- `ProjectController.showCreateSubProjectForm()` (lines 140-156)
- `ProjectController.saveSubProject()` (lines 220-245)
- `TaskController.createTask()` (lines 111-125)
- `TaskController.createSubTask()` (lines 435-451)
- `TaskController.editTask()` (similar pattern)
- `TaskController.editSubTask()` (similar pattern)

**Debt Level**: HIGH
**Effort**: Medium
**Recommendation**: Extract to `ValidationService` or use Bean Validation

#### Employee Header Setup
**Location**: Multiple controller methods
**Issue**: Repeated code for adding employee info to model
**Pattern**:
```java
Employee employee = employeeService.getEmployeeById(employeeId);
if (employee != null) {
    model.addAttribute("username", employee.getUsername());
    model.addAttribute("employeeRole", employee.getRole());
}
```

**Debt Level**: MEDIUM
**Effort**: Low
**Recommendation**: Extract to `@ModelAttribute` or utility method

#### Date Range Validation
**Location**: Edit methods in controllers
**Issue**: Complex validation logic duplicated
**Files**:
- `ProjectController.editSubProject()` (lines 323-361)
- `TaskController.editTask()` (lines 276-352)
- `TaskController.editSubTask()` (lines 593-637)

**Debt Level**: HIGH
**Effort**: Medium
**Recommendation**: Extract to validation service

### 2. Inconsistent Error Handling

**Issue**: Mix of approaches
- Some return `null`
- Some use try-catch with `System.out.println()`
- Some return error messages in Model
- Some throw exceptions

**Debt Level**: MEDIUM
**Effort**: High
**Recommendation**: Implement consistent exception handling strategy

### 3. Magic Numbers and Strings

**Issue**: Hardcoded values throughout codebase
- Year range: `2000`, `2100`
- Status strings
- Priority strings
- Error messages

**Debt Level**: LOW
**Effort**: Low
**Recommendation**: Extract to constants or configuration

### 4. Missing Transaction Management

**Issue**: No explicit transaction boundaries
**Impact**: Potential data inconsistency
**Example**: `ProjectRepository.deleteProject()` performs multiple deletes without transaction

**Debt Level**: HIGH
**Effort**: Medium
**Recommendation**: Add `@Transactional` annotations

### 5. No Input Validation Framework

**Issue**: Manual validation scattered in controllers
**Debt Level**: MEDIUM
**Effort**: Medium
**Recommendation**: Implement Bean Validation (JSR-303)

### 6. Plain Text Passwords

**Issue**: Critical security vulnerability
**Debt Level**: CRITICAL
**Effort**: Medium
**Recommendation**: Implement password hashing immediately

### 7. No Logging Framework

**Issue**: Using `System.out.println()` for logging
**Debt Level**: MEDIUM
**Effort**: Low
**Recommendation**: Implement SLF4J/Logback

### 8. Inconsistent Naming

**Issue**: Mixed naming conventions
- `projectID` vs `projectId`
- `employeeId` vs `employee_id` (in SQL)

**Debt Level**: LOW
**Effort**: Low
**Recommendation**: Standardize naming conventions

### 9. Missing Documentation

**Issue**:
- Limited JavaDoc
- No API documentation
- Business rules not documented

**Debt Level**: LOW
**Effort**: Medium
**Recommendation**: Add JavaDoc and API documentation

### 10. No Caching Strategy

**Issue**: Repeated database queries for same data
**Example**: Loading employee info multiple times

**Debt Level**: LOW
**Effort**: Medium
**Recommendation**: Implement caching for frequently accessed data

## Refactoring Priorities

### Immediate (Critical)
1. ✅ Remove debug code
2. ✅ Remove commented code
3. ✅ Implement password hashing
4. ✅ Add transaction management

### Short Term (High Priority)
1. Extract date validation logic
2. Extract employee header setup
3. Implement proper logging
4. Add input validation framework
5. Standardize error handling

### Medium Term
1. Remove code duplication
2. Extract constants
3. Standardize naming
4. Add documentation
5. Implement caching

### Long Term
1. Consider migrating to JPA
2. Refactor complex methods
3. Add comprehensive tests
4. Performance optimization

## Code Quality Metrics to Track

1. **Cyclomatic Complexity**: Target < 10 per method
2. **Code Duplication**: Target < 3%
3. **Test Coverage**: Target > 80%
4. **Technical Debt Ratio**: Track and reduce over time

## Recommendations for Managing Technical Debt

1. **Create Technical Debt Backlog**: Track all items
2. **Prioritize by Impact**: Critical → High → Medium → Low
3. **Allocate Time**: Dedicate 20% of development time to debt reduction
4. **Refactor Incrementally**: Don't try to fix everything at once
5. **Prevent New Debt**: Code reviews, linting, static analysis
6. **Document Decisions**: Why certain approaches were taken

## Tools to Help

1. **SonarQube**: Code quality analysis
2. **PMD/Checkstyle**: Code style checking
3. **JaCoCo**: Test coverage
4. **ArchUnit**: Architecture testing
5. **SpotBugs**: Bug detection
