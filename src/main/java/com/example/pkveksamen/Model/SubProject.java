package com.example.pkveksamen.Model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SubProject {

    private long subProjectID;
    private String subProjectName;
    private String subProjectDescription;
    private String subProjectStatus;
    private int subProjectDuration;
    private LocalDate startDate;
    private LocalDate endDate;


    public SubProject(long subProjectID, String subProjectName, String subProjectDescription, String subProjectStatus, int subProjectDuration, LocalDate endDate, LocalDate startDate) {
        this.subProjectID = subProjectID;
        this.subProjectName = subProjectName;
        this.subProjectDescription = subProjectDescription;
        this.subProjectStatus = subProjectStatus;
        this.subProjectDuration = subProjectDuration;
        this.endDate = endDate;
        this.startDate = startDate;
    }

    public SubProject() {

    }

    public void recalculateDuration() {
        if (startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate);

            // Hvis I vil tælle begge dage med:
            // days = days + 1;

            if (days < 0) {
                subProjectDuration = 0; // eller kast exception, hvis det er “ulovligt”
            } else {
                subProjectDuration = (int) days;
            }
        } else {
            subProjectDuration = 0;
        }
    }


    public long getSubProjectID() {
        return subProjectID;
    }

    public void setSubProjectID(long subProjectID) {
        this.subProjectID = subProjectID;
    }

    public String getSubProjectName() {
        return subProjectName;
    }

    public void setSubProjectName(String subProjectName) {
        this.subProjectName = subProjectName;
    }

    public String getSubProjectDescription() {
        return subProjectDescription;
    }

    public void setSubProjectDescription(String subProjectDescription) {
        this.subProjectDescription = subProjectDescription;
    }

    public String getSubProjectStatus() {
        return subProjectStatus;
    }

    public void setSubProjectStatus(String subProjectStatus) {
        this.subProjectStatus = subProjectStatus;
    }

    public int getSubProjectDuration() {
        return subProjectDuration;
    }

    public void setSubProjectDuration(int subProjectDuration) {
        this.subProjectDuration = subProjectDuration;
    }
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}
