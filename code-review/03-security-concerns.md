# Security Concerns

## Critical Security Issues

### 1. Hardcoded Database Credentials Committed to the Repo
**Severity**: CRITICAL  
**Location**: `src/main/resources/application-mysql.properties`, `src/main/resources/application.properties`

**Issue**:
- Real database endpoint + username + password are committed in `application-mysql.properties`
- `application.properties` sets `spring.profiles.active=mysql`, so the committed credentials are the default runtime configuration

**Risk**:
- Credential leak enables unauthorized database access
- Requires credential rotation and incident response if this repo was shared/cloned
- Increases likelihood of accidentally pointing local/dev runs at a shared/production-like database

**Recommendation**:
- Remove secrets from version control and rotate the leaked credentials immediately
- Keep only placeholders in repo (e.g., `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`) and load real values from environment/secret store
- Avoid committing `spring.profiles.active` defaults; set profile via environment (e.g., `SPRING_PROFILES_ACTIVE`)

### 2. Broken Authentication / Access Control (IDOR Across App)
**Severity**: CRITICAL  
**Location**: Controllers that accept `employeeId` in URL/query params (e.g., `EmployeeController.validateLogin()`, `ProjectController`, `TaskController`)

**Issue**:
- “Login” returns an `employeeId` and then uses it as the primary proof of identity via URLs
- There is no server-side check that the caller is authenticated as that employee
- Many endpoints can be accessed by changing `{employeeId}` in the URL (Insecure Direct Object Reference)

**Risk**:
- Any user (or unauthenticated visitor) can access or modify other users’ data by guessing/changing IDs
- Role checks become meaningless because the role is looked up using the attacker-supplied `employeeId`

**Recommendation**:
- Implement real authentication and authorization (typically Spring Security)
- Store the authenticated user identity in the session/security context; do not take `employeeId` from the URL as “who am I”
- Enforce authorization checks based on the authenticated principal (e.g., “is member of project”, “is project owner”, “is manager”)

### 3. Plain Text Password Storage
**Severity**: CRITICAL
**Location**: `EmployeeRepository.createEmployee()`, `EmployeeRepository.validateLogin()`

**Current Implementation**:
```java
// Passwords stored and compared in plain text
ps.setString(2, password); // No hashing!
```

**Risk**:
- If database is compromised, all passwords are exposed
- Passwords visible to database administrators
- Violates security best practices and compliance requirements

**Recommendation**:
```java
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// In EmployeeRepository
private final BCryptPasswordEncoder passwordEncoder;

public void createEmployee(String username, String password, ...) {
    String hashedPassword = passwordEncoder.encode(password);
    // Store hashedPassword instead of password
}
```

### 4. No Password Complexity Requirements
**Severity**: HIGH
**Location**: `EmployeeController.createEmployeePost()`

**Issue**: No validation for password strength
- No minimum length requirement
- No complexity requirements (uppercase, numbers, special chars)

**Recommendation**: Add password validation rules

### 5. Session Management / CSRF
**Severity**: HIGH
**Location**: `EmployeeController.validateLogin()`, `EmployeeController.logout()`

**Issues**:
- “Login” does not establish a server-side authenticated session (it redirects with an ID)
- No CSRF protection (no Spring Security; forms don’t include CSRF tokens)
- Session timeout and cookie hardening not configured/verified
- Employee ID passed in URL (visible in browser history)

**Current Implementation**:
```java
return "redirect:/project/list/" + id; // ID in URL
```

**Recommendation**:
- Store user identity server-side (session/security context) instead of URL parameters
- Add CSRF protection (Spring Security provides this by default)
- Configure session timeout and secure cookie settings

### 6. Self-Service Role Assignment (Privilege Escalation)
**Severity**: HIGH  
**Location**: `EmployeeController.createEmployee()`, `EmployeeController.createEmployeePost()`, `src/main/resources/templates/create-employee.html`

**Issue**:
- The registration flow is publicly accessible and allows selecting a role during sign-up
- A user can self-register as `PROJECT_MANAGER` (or any role exposed by the UI) with no authorization gate

**Risk**:
- Privilege escalation: anyone can grant themselves elevated access
- Even after fixing “ID in URL”, this still leaves a path to unauthorized manager access

**Recommendation**:
- Default new users to the least-privileged role server-side (ignore any submitted role)
- Restrict role assignment to an admin-only workflow
- Consider removing open registration entirely if not required

### 7. SQL Injection Prevention
**Severity**: LOW (Mostly Mitigated)
**Status**: ✅ Good - Using PreparedStatements throughout

**Note**: Continue using parameterized queries. Avoid string concatenation.

### 8. Input Validation
**Severity**: MEDIUM

**Issues**:
- Limited input validation
- No validation for SQL injection patterns
- No length limits on user inputs
- Date validation is basic (only year range)

**Examples of Missing Validation**:
- Username: No length limits, character restrictions
- Email: No format validation
- Project/Task names: No length limits
- Descriptions: No length limits (could cause database issues)

**Recommendation**: Implement comprehensive input validation

### 9. Authorization Checks (Project/Task Ownership & Membership)
**Severity**: HIGH

**Issues**:
- Role checks exist but may not be comprehensive
- No check if user can access specific project/task
- Employee ID in URL can be manipulated

**Example**:
```java
@GetMapping("/project/list/{employeeId}")
public String showProjectsByEmployeeId(@PathVariable int employeeId, ...) {
    // No verification that logged-in user matches employeeId
    // User could access other users' projects by changing URL
}
```

**Recommendation**:
- Verify access using authenticated principal, not a user-supplied `employeeId`
- Add authorization checks for project/task access (ownership/membership)
- Use Spring Security for method-level security (e.g., `@PreAuthorize`)

### 10. Error Information Disclosure
**Severity**: LOW

**Issues**:
- Stack traces printed to console
- Error messages may reveal system internals

**Example**:
```java
catch (Exception e) {
    System.out.println("Uventet fejl ved oprettelse af bruger: " + e.getMessage());
    e.printStackTrace(); // Stack trace in logs
}
```

**Recommendation**:
- Use proper logging framework (SLF4J/Logback)
- Don't expose internal errors to users
- Log detailed errors server-side only

### 11. Insecure Defaults: Seeded Users & Weak Test Passwords
**Severity**: HIGH  
**Location**: `src/main/resources/data.sql`

**Issue**:
- `data.sql` seeds users with known/weak passwords (e.g. `admin123`, `dev123`)
- If `data.sql` runs in a deployed environment (intentionally or accidentally), it creates predictable accounts

**Risk**:
- Immediate account compromise if seeded credentials exist in any shared environment

**Recommendation**:
- Ensure test/demo seed data never runs in production (use profile-guarded init or separate `data-<profile>.sql`)
- Remove seeded “admin” accounts or generate one-time randomized credentials out-of-band

### 12. H2 Console Enabled (If H2 Profile Is Ever Used)
**Severity**: MEDIUM  
**Location**: `src/main/resources/application-h2.properties`

**Issue**:
- H2 console is enabled at `/h2-console`

**Risk**:
- If enabled in a non-local environment, it can expose database contents and become an attack surface

**Recommendation**:
- Disable H2 console outside local dev, or strictly protect it (network restrictions + authentication)

### 13. HTTPS/Encryption
**Severity**: MEDIUM

**Issue**: No mention of HTTPS configuration
- Passwords transmitted over HTTP (if not using HTTPS)
- Session cookies not secure

**Recommendation**:
- Configure HTTPS in production
- Set secure cookie flags
- Use HSTS headers

### 14. Database Transport Security Settings
**Severity**: MEDIUM  
**Location**: `src/main/resources/application-mysql.properties`

**Issue**:
- JDBC URL includes `useSSL=true&requireSSL=false` which can permit non-required TLS depending on driver/server behavior

**Risk**:
- Potential downgrade/MITM risk if TLS is not strictly required/validated

**Recommendation**:
- Require TLS to the DB in production (`requireSSL=true`) and verify certificates per environment requirements

## Security Best Practices Missing

1. **No Rate Limiting**: Login attempts not rate-limited (vulnerable to brute force)
2. **No Account Lockout**: No protection against repeated failed login attempts
3. **No Password Reset**: No password reset functionality
4. **No Audit Logging**: No logging of security events (login attempts, access changes)
5. **No Input Sanitization**: HTML/script injection possible in user inputs
6. **No Content Security Policy**: XSS protection not configured
7. **No Secret Management Process**: Credentials/secrets are stored in repo config files

## Recommendations Priority

### Immediate (Critical)
1. ✅ Remove committed credentials + rotate DB password
2. ✅ Implement real authentication/authorization (Spring Security)
3. ✅ Implement password hashing (BCrypt/Argon2)
4. ✅ Remove `employeeId` as an authentication mechanism (stop trusting IDs in URLs)

### Short Term (High Priority)
1. Lock down registration + role assignment (no self-service manager role)
2. Add input validation framework
3. Implement authorization checks for project/task access
4. Add password complexity requirements
5. Configure HTTPS

### Medium Term
1. Add rate limiting for login
2. Implement audit logging
3. Add CSRF protection
4. Input sanitization for XSS prevention
