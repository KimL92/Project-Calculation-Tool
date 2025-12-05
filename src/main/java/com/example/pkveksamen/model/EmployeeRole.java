package com.example.pkveksamen.model;
public enum EmployeeRole {
    PROJECT_MANAGER("Project Manager"),
    TEAM_MEMBER("Team Member");

    private final String displayName;

    EmployeeRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EmployeeRole fromDisplayName(String displayName) {
        for (EmployeeRole role : EmployeeRole.values()) {
            if (role.getDisplayName().equalsIgnoreCase(displayName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant with display name " + displayName);
    }
}
