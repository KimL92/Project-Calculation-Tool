package com.aljamour.pkveksamen.Model;

public class SubProject {
    private long subProjectID;
    private String subProjectName;
    private String subProjectDescription;
    private String subProjectStatus;
    private int subProjectDuration;

    public SubProject (long subProjectID, String subProjectName, String subProjectDescription, String subProjectStatus, int subProjectDuration) {
        this.subProjectID = subProjectID;
        this.subProjectName = subProjectName;
        this.subProjectDescription = subProjectDescription;
        this.subProjectStatus = subProjectStatus;
        this.subProjectDuration = subProjectDuration;
    }
}
