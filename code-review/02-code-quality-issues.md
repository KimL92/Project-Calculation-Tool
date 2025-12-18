# Code Quality Issues

## Critical Issues

### 1. Password Storage - Security Vulnerability
**Location**: `EmployeeRepository.createEmployee()`, `EmployeeRepository.validateLogin()`
**Issue**: Passwords are stored in plain text
```java
// Current implementation stores password as plain text
jdbcTemplate.update(connection -> {
    PreparedStatement ps = connection.prepareStatement(insertEmployeeSql, Statement.RETURN_GENERATED_KEYS);
    ps.setString(2, password); // Plain text password!
    ...
});
```
**Impact**: HIGH - Security vulnerability
**Recommendation**: Use BCrypt or Argon2 for password hashing

### 2. SQL Injection Risk (Partially Mitigated)
**Location**: All repository classes
**Issue**: While using PreparedStatements (good), some string concatenation exists
**Status**: Most queries use parameterized queries correctly
**Recommendation**: Continue using PreparedStatements, avoid string concatenation

### 3. No Input Validation Framework
**Location**: Controllers
**Issue**: Manual validation scattered throughout controllers
**Example**: Date range validation (2000-2100) hardcoded in multiple places
**Recommendation**: Use Bean Validation (JSR-303) with `@Valid` annotations

## Code Smells

### 1. Code Duplication
**Location**: Multiple controllers
**Issue**: Repeated validation logic, error handling patterns
**Examples**:
- Date validation (2000-2100) repeated in:
  - `ProjectController.createProject()`
  - `ProjectController.showCreateSubProjectForm()`
  - `TaskController.createTask()`
  - `TaskController.createSubTask()`
- Employee header setup repeated in many controller methods
- Date range validation logic duplicated across edit methods

**Recommendation**: Extract to utility classes or use AOP

### 2. Magic Numbers and Strings
**Location**: Throughout codebase
**Examples**:
- Year range: `2000` and `2100` hardcoded
- Status strings: `"Not started"`, `"In progress"`, `"Completed"`
- Priority strings: `"Low"`, `"Medium"`, `"High"`

**Recommendation**: Use constants or configuration properties

### 3. Inconsistent Error Handling
**Location**: Controllers and Services
**Issue**: Mix of exception handling approaches
- Some methods return `null` on error
- Some use try-catch with `System.out.println()`
- Some return error messages in Model

**Example**:
```java
// EmployeeService.createEmployee()
catch (Exception e) {
    System.out.println("Uventet fejl ved oprettelse af bruger: " + e.getMessage());
    e.printStackTrace();
    return false;
}
```

**Recommendation**: Implement consistent exception handling strategy

### 4. Debug Code in Production
**Location**: `TaskController.createTask()`
```java
// Debug - fjern denne linje efter test
System.out.println("DEBUG - SubProjectId: " + subProjectId + ", SubProject: " + subProject);
```
**Issue**: Debug statements left in code
**Recommendation**: Remove or use proper logging framework

### 5. Commented-Out Code
**Location**: Multiple files
**Examples**:
- `ProjectController.java`: Commented methods (lines 24-33, 207, 523-535)
- `ProjectRepository.java`: Commented delete method (lines 112-116)

**Recommendation**: Remove commented code or document why it's kept

## Naming Conventions

### Issues
1. **Inconsistent naming**: `projectID` vs `projectId` (camelCase inconsistency)
2. **Mixed language**: Some comments in Danish, code in English
3. **Abbreviations**: `PKV` in package name unclear

### Recommendations
- Use consistent camelCase for all IDs
- Standardize on English for code and comments
- Use descriptive package names

## Method Complexity

### High Complexity Methods
1. **`TaskController.createTask()`** (lines 97-200)
   - Multiple validation checks
   - Complex conditional logic
   - **Recommendation**: Extract validation to separate methods

2. **`TaskController.editTask()`** (lines 255-356)
   - Repeated validation blocks
   - **Recommendation**: Extract validation logic

3. **`TaskController.editSubTask()`** (lines 574-643)
   - Similar pattern to `editTask()`
   - **Recommendation**: Create shared validation utility

## Null Safety

### Issues
- Many null checks scattered throughout code
- No use of Optional<T> for nullable returns
- Potential NullPointerException risks

**Example**:
```java
if (employee != null) {
    model.addAttribute("username", employee.getUsername());
    // ...
}
```

**Recommendation**: Use Optional<T> or @Nullable/@NonNull annotations

## Resource Management

### Issues
- No explicit transaction management
- Potential for partial updates if operations fail
- No rollback strategy

**Recommendation**: Use `@Transactional` annotations for multi-step operations
