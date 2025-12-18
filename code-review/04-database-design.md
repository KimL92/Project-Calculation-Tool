# Database Design Review

## Schema Overview

The database uses a hierarchical structure:
```
Employee
  ↓
Project (created by employee)
  ↓
SubProject (belongs to Project)
  ↓
Task (belongs to SubProject, assigned to Employee)
  ↓
SubTask (belongs to Task)
```

## Strengths

1. **Clear Relationships**: Well-defined foreign key relationships
2. **Cascade Deletes**: Properly configured CASCADE deletes for dependent records
3. **Referential Integrity**: Foreign key constraints maintain data integrity
4. **Normalization**: Reasonable normalization (3NF)

## Issues and Concerns

### 1. Missing Indexes
**Issue**: No indexes defined on frequently queried columns

**Impact**: Performance degradation as data grows

**Recommendations**:
```sql
-- Add indexes for common queries
CREATE INDEX idx_project_employee_id ON project(employee_id);
CREATE INDEX idx_task_employee_id ON task(employee_id);
CREATE INDEX idx_task_sub_project_id ON task(sub_project_id);
CREATE INDEX idx_sub_task_task_id ON sub_task(task_id);
CREATE INDEX idx_project_employee_project_id ON project_employee(project_id);
CREATE INDEX idx_project_employee_employee_id ON project_employee(employee_id);
CREATE INDEX idx_employee_username ON employee(username);
CREATE INDEX idx_employee_email ON employee(email);
```

### 2. Duration Field Redundancy
**Issue**: `duration` fields stored in database but also calculated in Java

**Location**:
- `project` table (no duration column, calculated in Java)
- `sub_project.sub_project_duration`
- `task.task_duration`
- `sub_task.sub_task_duration`

**Problem**:
- Duration can become inconsistent if dates change but duration isn't recalculated
- Storing calculated values violates normalization principles

**Current Behavior**:
```java
// Duration calculated in Java
project.recalculateDuration();
// But also stored in database for sub_projects, tasks, subtasks
```

**Recommendation**:
- Option 1: Remove duration columns, calculate on-the-fly
- Option 2: Use database triggers to auto-calculate
- Option 3: Use computed columns (if database supports)

### 3. Status and Priority Storage
**Issue**: Storing enum values as VARCHAR

**Current**:
```sql
task_status VARCHAR(50) NOT NULL,
task_priority VARCHAR(50),
```

**Problems**:
- No referential integrity
- Typos possible
- Larger storage than needed
- No database-level validation

**Recommendation**:
- Option 1: Use ENUM type (MySQL supports)
- Option 2: Create reference tables (more flexible)
- Option 3: Keep VARCHAR but add CHECK constraints

### 4. Missing Constraints

#### Date Validation
**Issue**: No database-level constraints for date relationships

**Examples**:
- `task_deadline` should be >= `task_start_date`
- `sub_project_deadline` should be within project dates
- `task` dates should be within `sub_project` dates

**Recommendation**: Add CHECK constraints
```sql
ALTER TABLE task ADD CONSTRAINT chk_task_dates
    CHECK (task_deadline >= task_start_date);
```

#### Required Fields
**Issue**: Some fields should be NOT NULL but aren't

**Examples**:
- `task.task_priority` - nullable but should have default
- `task.task_status` - has default in code but not in schema

### 5. Role Table Design
**Issue**: `role` table exists but `employee.role` is VARCHAR

**Current Schema**:
```sql
CREATE TABLE role (
    role_id BIGINT ...,
    role_name VARCHAR(100) NOT NULL UNIQUE,
    ...
);

CREATE TABLE employee (
    ...
    role VARCHAR(50) NOT NULL  -- Stored as string, not FK!
);
```

**Problem**:
- Two different role systems:
  1. `employee.role` (EmployeeRole: PROJECT_MANAGER, TEAM_MEMBER)
  2. `role` table (AlphaRole: skills)
- Inconsistent design
- `employee.role` should reference a role table or be an enum

**Recommendation**:
- Create `employee_role_type` table for PROJECT_MANAGER/TEAM_MEMBER
- Or use ENUM type
- Keep `role` table for AlphaRole (skills) only

### 6. Password Storage
**Issue**: `password VARCHAR(255)` - currently plain text

**Recommendation**:
- Increase size to accommodate hashed passwords (BCrypt needs 60 chars)
- Add password reset token fields if implementing password reset

### 7. Missing Audit Fields
**Issue**: No tracking of creation/modification

**Recommendation**: Add audit columns
```sql
ALTER TABLE project ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE project ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE project ADD COLUMN created_by BIGINT;
-- Similar for other tables
```

### 8. Soft Deletes
**Issue**: Hard deletes only - no way to recover deleted data

**Recommendation**: Consider soft deletes for important entities
```sql
ALTER TABLE project ADD COLUMN deleted_at TIMESTAMP NULL;
ALTER TABLE project ADD COLUMN deleted_by BIGINT NULL;
```

### 9. Data Type Inconsistencies
**Issue**: Mixed use of BIGINT and INT

**Examples**:
- `employee.employee_id`: BIGINT
- `task.employee_id`: BIGINT
- `task.task_id`: BIGINT
- But Java code uses `int` in some places

**Recommendation**: Standardize on BIGINT for IDs (future-proofing)

### 10. Missing Unique Constraints
**Issue**: Some fields should be unique but aren't

**Examples**:
- `project.project_title` - could be unique per customer
- `sub_project.sub_project_title` - could be unique per project

**Recommendation**: Add unique constraints where business logic requires

## Query Performance Issues

### 1. N+1 Query Problem
**Location**: `TaskService.showTaskByEmployeeId()`

**Issue**: Loading alpha roles for each task's employee separately
```java
for (Task task : tasks) {
    if (task.getAssignedEmployee() != null) {
        task.getAssignedEmployee().setAlphaRoles(
            employeeRepository.findAlphaRolesByEmployeeId(...) // N queries!
        );
    }
}
```

**Recommendation**: Use JOIN or batch loading

### 2. UNION Queries
**Location**: `ProjectRepository.showProjectsByEmployeeId()`

**Issue**: UNION query could be optimized
```sql
SELECT ... WHERE p.employee_id = ?
UNION
SELECT ... WHERE pe.employee_id = ?
```

**Recommendation**: Use LEFT JOIN or OR condition

## Migration Recommendations

1. **Add indexes** for performance
2. **Add CHECK constraints** for data validation
3. **Standardize ID types** (all BIGINT)
4. **Add audit fields** (created_at, updated_at)
5. **Consider soft deletes** for critical data
6. **Fix role storage** (use proper FK or ENUM)
7. **Remove or properly manage duration fields**

## Database-Specific Notes

### H2 Database
- Used for development/testing
- Some MySQL-specific features may not work
- Consider using H2 compatibility mode

### MySQL
- Production database
- Can use ENUM types
- Supports CHECK constraints (MySQL 8.0.16+)
- Consider using InnoDB engine explicitly
