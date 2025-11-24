package com.aljamour.pkveksamen.Model;

public enum UserRole {
    PROJECTLEDER("Projectleder"),
    TEAMMEDLEM("Teammedlem"),
    ADMIN("Administrator");


    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }


    public String GetDisplayName(){
        return displayName;
    }

}
