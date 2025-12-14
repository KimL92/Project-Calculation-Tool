# Architecture Overview

## Project Summary
This is a Spring Boot web application for project management with role-based access control. The system allows Project Managers and Team Members to collaborate on projects, sub-projects, tasks, and subtasks.

## Technology Stack
- **Framework**: Spring Boot 3.5.7
- **Java Version**: 17
- **Database**: H2 (development) / MySQL (production)
- **Template Engine**: Thymeleaf
- **Data Access**: Spring JDBC (JdbcTemplate)
- **Build Tool**: Maven

## Architecture Pattern
The application follows a **layered architecture** pattern:

```
Controller Layer (MVC)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Database (H2/MySQL)
```

## Key Components

### Controllers
- `EmployeeController`: Handles authentication, employee creation, and login/logout
- `ProjectController`: Manages projects, sub-projects, and project members
- `TaskController`: Handles tasks, subtasks, status updates, and notes

### Services
- `EmployeeService`: Employee management and authentication
- `ProjectService`: Project and sub-project business logic
- `TaskService`: Task and subtask business logic

### Repositories
- `EmployeeRepository`: Employee data access
- `ProjectRepository`: Project and sub-project data access
- `TaskRepository`: Task and subtask data access

### Models
- `Employee`: User entity with roles and skills
- `Project`: Main project entity
- `SubProject`: Sub-project entity (belongs to Project)
- `Task`: Task entity (belongs to SubProject)
- `SubTask`: Subtask entity (belongs to Task)

## Data Flow
1. **Request** → Controller receives HTTP request
2. **Validation** → Controller validates input (basic validation present)
3. **Business Logic** → Service layer processes business rules
4. **Data Access** → Repository executes SQL queries via JdbcTemplate
5. **Response** → Controller returns Thymeleaf view or redirect

## Database Schema
- **employee**: User accounts with roles
- **role**: Alpha roles (skills) reference table
- **employee_role**: Many-to-many relationship between employees and roles
- **project**: Main projects
- **project_employee**: Many-to-many relationship between projects and employees
- **sub_project**: Sub-projects belonging to projects
- **task**: Tasks belonging to sub-projects
- **sub_task**: Subtasks belonging to tasks

## Strengths
1. Clear separation of concerns (Controller → Service → Repository)
2. Consistent use of dependency injection
3. Well-structured package organization
4. Support for multiple database profiles (H2, MySQL, production)

## Areas for Improvement
1. No use of JPA/Entity relationships (using plain JDBC)
2. Manual SQL query construction (no ORM)
3. Limited use of Spring Boot features (no Spring Data JPA)
4. No transaction management annotations
5. Direct database access in repositories without abstraction
