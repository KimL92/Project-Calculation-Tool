package com.aljamour.pkveksamen.Model;

public enum UserRole {
    PROJECT_MANAGER("Project Manager"),
    TEAM_MEMBER("Team Member"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
