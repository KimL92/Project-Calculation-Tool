# UI/UX Issues Review

## Overview
This document identifies UI/UX issues found in the Thymeleaf templates and CSS files of the Project Calculation Tool application.

## Critical Issues

### 1. Missing CSRF Protection in Forms
**Severity**: CRITICAL (Security)
**Location**: All HTML templates with forms

**Issue**: All POST forms are missing CSRF tokens, making them vulnerable to Cross-Site Request Forgery attacks.

**Examples**:
- `login.html` line 18: `<form th:action="@{/validate-login}" method="post">`
- `create-employee.html` line 13: `<form th:action="@{/create-employee}" ... method="post">`
- `project.html` line 21: `<form th:action="@{/logout}" method="post">`
- All edit/create forms throughout the application

**Impact**:
- Users can be tricked into performing actions without their knowledge
- Security vulnerability that could lead to unauthorized actions

**Fix**:
```html
<!-- Add CSRF token to all POST forms -->
<form th:action="@{/validate-login}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
    <!-- rest of form -->
</form>
```

Or use Spring Security's automatic CSRF token injection with Thymeleaf.

### 2. Missing Viewport Meta Tag
**Severity**: HIGH
**Location**: All HTML templates

**Issue**: No viewport meta tag in any template, causing poor mobile responsiveness.

**Current**: No viewport meta tag present

**Fix**:
```html
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>...</title>
</head>
```

**Impact**:
- Poor mobile user experience
- Content may not scale properly on mobile devices
- Accessibility issues for users with visual impairments

## High Priority Issues

### 3. Language Inconsistency
**Severity**: HIGH
**Location**: All HTML templates

**Issue**: Inconsistent `lang` attributes across templates:
- `homepage.html`: `lang="da"` (Danish)
- `login.html`: `lang="en"` (English)
- `view-all-employees.html`: `lang="en"` (English)
- Most other templates: `lang="da"` (Danish)

**Impact**:
- Screen readers may use wrong language
- Browser language detection fails
- SEO issues
- Accessibility problems

**Fix**: Standardize on one language (preferably English for international use) or implement proper i18n:
```html
<html lang="en" xmlns:th="http://www.thymeleaf.org">
```

### 4. Mixed Language Content
**Severity**: HIGH
**Location**: Multiple templates

**Issue**: Content mixes Danish and English:
- `homepage.html` line 17: "administration og projects" (Danish "og" = English "of")
- `login.html` line 45: "Haven't signed up yet? Register above" (English)
- `task.html` line 190: "Er du sikker på, du vil slette denne task?" (Danish)
- `createproject.html` line 5: "Create new projekt" (typo: should be "project")
- `createproject.html` line 10: "Create new projekt" (typo)

**Examples**:
- Typo: "projekt" should be "project"
- Typo: "og" should be "of" in "administration og projects"
- Mixed: Danish confirmation messages in English interface

**Fix**:
1. Standardize on one language
2. Fix typos: "projekt" → "project", "og" → "of"
3. Use consistent language for all user-facing text

### 5. No Accessibility Attributes
**Severity**: HIGH
**Location**: All templates

**Issue**: Missing accessibility attributes:
- No `aria-label` attributes on buttons/links
- No `alt` text for icons (SVG icons have no descriptions)
- No `role` attributes where needed
- No `aria-describedby` for form fields with error messages
- No `aria-required` for required fields
- Missing `aria-live` regions for dynamic content

**Examples**:
```html
<!-- Current: No accessibility attributes -->
<button type="submit" class="logout-button">
    <svg>...</svg>
    Log out
</button>

<!-- Should be: -->
<button type="submit" class="logout-button" aria-label="Log out">
    <svg aria-hidden="true">...</svg>
    Log out
</button>
```

**Impact**:
- Poor experience for screen reader users
- WCAG compliance issues
- Legal accessibility requirements not met

**Fix**: Add appropriate ARIA attributes throughout:
```html
<button type="submit" aria-label="Delete project" onclick="...">
<input type="text" aria-required="true" aria-describedby="username-error">
<span id="username-error" class="error" role="alert" th:if="${error}">...</span>
```

### 6. Inline Styles (Bad Practice)
**Severity**: MEDIUM
**Location**: Multiple templates

**Issue**: Inline styles used instead of CSS classes:
- `project.html` line 112: `style="display:inline"`
- `view-project-members.html` line 114: `style="display:inline; margin-left: 10px;"`
- `view-project-members.html` line 166: `style="display:inline;"`
- `task.html` line 188: `style="display:inline"`

**Impact**:
- Harder to maintain
- Inconsistent styling
- Difficult to override with CSS
- Violates separation of concerns

**Fix**: Move to CSS classes:
```html
<!-- Instead of: -->
<form style="display:inline;">

<!-- Use: -->
<form class="inline-form">
```

### 7. Commented CSS Code
**Severity**: MEDIUM
**Location**: `homepage.css`

**Issue**: Large block of commented-out CSS code (lines 112-194 in `homepage.css`), approximately 82 lines of dead code.

**Impact**:
- Increases file size
- Confusing for developers
- Maintenance burden

**Fix**: Remove commented code or document why it's kept.

### 8. Inconsistent Error Message Display
**Severity**: MEDIUM
**Location**: Multiple templates

**Issue**: Error messages displayed inconsistently:
- `login.html`: Uses `.error-message` class
- `create-employee.html`: Uses `.error-message` class but different styling
- `edit-task.html`: Uses `.error-banner` class
- Some forms show errors, others don't

**Impact**:
- Inconsistent user experience
- Users may miss important error messages

**Fix**: Standardize error message display across all templates:
```html
<div th:if="${error}" class="error-message" role="alert">
    <p th:text="${error}"></p>
</div>
```

## Medium Priority Issues

### 9. Missing Form Validation Feedback
**Severity**: MEDIUM
**Location**: All form templates

**Issue**:
- No client-side validation feedback (only HTML5 `required`)
- No visual indication of validation state (invalid/valid)
- Error messages not associated with form fields using `aria-describedby`
- No success messages after form submission

**Impact**:
- Poor user experience
- Users may not understand why forms fail
- Accessibility issues

**Fix**: Add proper validation feedback:
```html
<div class="form-group">
    <label for="username">Username</label>
    <input type="text"
           id="username"
           th:field="*{username}"
           aria-required="true"
           aria-describedby="username-error"
           th:classappend="${#fields.hasErrors('username')} ? 'error'">
    <span id="username-error"
          class="error-message"
          role="alert"
          th:if="${#fields.hasErrors('username')}"
          th:errors="*{username}"></span>
</div>
```

### 10. Missing Loading States
**Severity**: MEDIUM
**Location**: Forms and action buttons

**Issue**: No loading indicators for form submissions or async operations.

**Impact**:
- Users may click buttons multiple times
- No feedback during processing
- Poor user experience

**Fix**: Add loading states:
```html
<button type="submit" class="submit-button" onclick="this.disabled=true; this.textContent='Submitting...';">
    Submit
</button>
```

### 11. No Skip Links for Keyboard Navigation
**Severity**: MEDIUM
**Location**: All templates

**Issue**: No skip links for keyboard users to skip navigation.

**Impact**:
- Poor keyboard navigation experience
- Accessibility issue

**Fix**: Add skip links:
```html
<body>
    <a href="#main-content" class="skip-link">Skip to main content</a>
    <!-- navigation -->
    <main id="main-content">
        <!-- content -->
    </main>
</body>
```

### 12. Table Accessibility Issues
**Severity**: MEDIUM
**Location**: `project.html`, `task.html`, `subproject.html`, etc.

**Issue**:
- Tables missing `<caption>` elements
- No `scope` attributes on table headers
- Complex tables without proper headers

**Example**:
```html
<!-- Current -->
<table class="project-table">
    <thead>
        <tr>
            <th>Title</th>
            <th>Description</th>
            <!-- ... -->
        </tr>
    </thead>
</table>

<!-- Should be -->
<table class="project-table">
    <caption>List of projects</caption>
    <thead>
        <tr>
            <th scope="col">Title</th>
            <th scope="col">Description</th>
            <!-- ... -->
        </tr>
    </thead>
</table>
```

### 13. Missing Focus Indicators
**Severity**: MEDIUM
**Location**: CSS files

**Issue**: Focus states may not be visible or properly styled for keyboard navigation.

**Impact**:
- Keyboard users can't see where they are
- Accessibility issue

**Fix**: Ensure all interactive elements have visible focus indicators:
```css
button:focus,
a:focus,
input:focus,
select:focus {
    outline: 2px solid #4b79a1;
    outline-offset: 2px;
}
```

### 14. Inconsistent Button Styling
**Severity**: LOW
**Location**: Multiple templates

**Issue**: Different button classes and styles across pages:
- `.login-button`
- `.create-employee-button`
- `.logout-button`
- `.btn`, `.btn-primary`, `.btn-danger`
- `.create-project-button`
- `.edit-button`, `.delete-button`

**Impact**:
- Inconsistent user experience
- Harder to maintain

**Fix**: Standardize button classes and create a design system.

### 15. Missing Breadcrumbs
**Severity**: LOW
**Location**: Most templates (except some edit pages)

**Issue**: No breadcrumb navigation in most pages, making it hard to understand navigation hierarchy.

**Impact**:
- Users may get lost
- Poor navigation experience

**Fix**: Add breadcrumb navigation:
```html
<nav aria-label="Breadcrumb">
    <ol>
        <li><a href="/">Home</a></li>
        <li><a href="/projects">Projects</a></li>
        <li aria-current="page">Project Details</li>
    </ol>
</nav>
```

### 16. No Empty State Messages
**Severity**: LOW
**Location**: Some templates

**Issue**: Some empty states exist (e.g., `project.html` line 90), but styling and messaging could be improved.

**Current**:
```html
<td colspan="7">You have no projects yet. Click "Create new project" for an overview.</td>
```

**Fix**: Create dedicated empty state components with helpful messaging and call-to-action buttons.

### 17. Missing Form Field Labels Association
**Severity**: MEDIUM
**Location**: Some forms

**Issue**: Some form fields may not be properly associated with labels using `for` and `id` attributes.

**Impact**:
- Screen readers may not announce labels
- Clicking label doesn't focus input
- Accessibility issue

**Fix**: Ensure all inputs have associated labels:
```html
<label for="username">Username</label>
<input type="text" id="username" name="username">
```

### 18. Inconsistent Date Formatting
**Severity**: LOW
**Location**: Templates displaying dates

**Issue**: Dates displayed using default formatting without localization:
```html
<td th:text="${project.projectStartDate}"></td>
```

**Impact**:
- Inconsistent date formats
- May not match user's locale expectations

**Fix**: Use Thymeleaf date formatting:
```html
<td th:text="${#temporals.format(project.projectStartDate, 'dd/MM/yyyy')}"></td>
```

## Recommendations Priority

### Immediate (Critical)
1. ✅ Add CSRF tokens to all POST forms
2. ✅ Add viewport meta tag to all templates
3. ✅ Fix language inconsistencies

### Short Term (High Priority)
1. Standardize language (choose English or Danish)
2. Fix typos ("projekt" → "project", "og" → "of")
3. Add accessibility attributes (ARIA labels, roles)
4. Remove inline styles
5. Standardize error message display

### Medium Term
1. Add form validation feedback
2. Improve table accessibility
3. Add loading states
4. Add skip links
5. Ensure focus indicators
6. Standardize button styling

### Long Term
1. Implement proper i18n (internationalization)
2. Create design system/component library
3. Add comprehensive accessibility testing
4. Implement client-side validation framework
5. Add breadcrumb navigation throughout

## Testing Recommendations

1. **Accessibility Testing**:
   - Use screen readers (NVDA, JAWS, VoiceOver)
   - Test keyboard navigation
   - Run automated accessibility tools (axe, WAVE)

2. **Responsive Testing**:
   - Test on multiple device sizes
   - Test on different browsers
   - Verify viewport meta tag works

3. **Cross-Browser Testing**:
   - Chrome, Firefox, Safari, Edge
   - Mobile browsers

4. **User Testing**:
   - Test with real users
   - Gather feedback on language clarity
   - Test error message clarity

## Tools and Resources

- **Accessibility**: WAVE, axe DevTools, Lighthouse
- **Responsive**: Browser DevTools, Responsive Design Mode
- **Validation**: HTML Validator, CSS Validator
- **i18n**: Spring's MessageSource, Thymeleaf's i18n support

## Code Examples for Fixes

### CSRF Token Example
```html
<form th:action="@{/create-employee}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
    <!-- form fields -->
</form>
```

### Accessible Form Example
```html
<div class="form-group">
    <label for="email">Email Address</label>
    <input type="email"
           id="email"
           th:field="*{email}"
           aria-required="true"
           aria-describedby="email-error email-help"
           th:classappend="${#fields.hasErrors('email')} ? 'error'">
    <span id="email-help" class="help-text">We'll never share your email</span>
    <span id="email-error"
          class="error-message"
          role="alert"
          th:if="${#fields.hasErrors('email')}"
          th:errors="*{email}"></span>
</div>
```

### Accessible Button Example
```html
<button type="submit"
        class="delete-button"
        aria-label="Delete project: Project Name"
        onclick="return confirm('Are you sure?');">
    <svg aria-hidden="true" focusable="false">
        <!-- icon -->
    </svg>
    <span class="sr-only">Delete</span>
</button>
```

---

**Review Date**: 2024
**Files Reviewed**: All HTML templates and CSS files in `src/main/resources/templates/` and `src/main/resources/static/`
