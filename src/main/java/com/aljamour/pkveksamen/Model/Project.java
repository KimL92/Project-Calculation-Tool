package com.aljamour.pkveksamen.Model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Project {

    private long projectID;
    private String projectName;
    private String projectDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private String projectCustomer;
    private int projectDuration;

    public void recalculateDuration() {
        if (startDate != null && endDate != null) {
            long days = ChronoUnit.DAYS.between(startDate, endDate);

            // Hvis I vil tælle begge dage med:
            // days = days + 1;

            if (days < 0) {
                projectDuration = 0; // eller kast exception, hvis det er “ulovligt”
            } else {
                projectDuration = (int) days;
            }
        } else {
            projectDuration = 0;
        }
    }


    public Project(){}

    public Project(long projectID, String projectName, String projectDescription, LocalDate startDate, LocalDate endDate,
                   String projectCustomer, int projectDuration, long subProjectID, String subProjectName,
                   String subProjectDescription, String subProjectStatus, int subProjectDuration) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectCustomer = projectCustomer;
        this.projectDuration = projectDuration;
    }


    public Project(long projectID, String projectName, String projectDescription, LocalDate startDate, LocalDate endDate,
                   String projectCustomer, int projectDuration) {
        this.projectID = projectID;
        this.projectName = projectName;
        this.projectDescription = projectDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.projectCustomer = projectCustomer;
        this.projectDuration = projectDuration;

    }

    public long getProjectID() {
        return projectID;
    }

    public void setProjectID(long projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
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

    public String getProjectCustomer() {
        return projectCustomer;
    }

    public void setProjectCustomer(String projectCustomer) {
        this.projectCustomer = projectCustomer;
    }

    public int getProjectDuration() {
        return projectDuration;
    }

    public void setProjectDuration(int projectDuration) {
        this.projectDuration = projectDuration;
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
}
