package com.example.pkveksamen.model;

public enum Status {
    NOT_STARTED("Not started"),
    IN_PROGRESS("In progress"),
    COMPLETED("Completed");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Status fromDisplayName(String displayName) {
        String normalizedInput = displayName.replace("_", " ").trim();

        for (Status status : Status.values()) {
            if (status.getDisplayName().equalsIgnoreCase(normalizedInput)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + displayName);
    }

}
