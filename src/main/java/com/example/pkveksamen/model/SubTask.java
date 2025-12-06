package com.example.pkveksamen.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class SubTask {
    private long subTaskId;
    private String subTaskName;
    private String subTaskDescription;
    private int subTaskDuration;
    private Status subTaskStatus;
    private Priority subTaskPriority;
    private String subTaskNote;
    private LocalDate subTaskStartDate;
    private LocalDate subTaskEndDate;

    public SubTask(){}

    public SubTask(long subTaskId, String subTaskName, String subTaskDescription, int subTaskDuration, Status subTaskStatus,
                   Priority subTaskPriority, String subTaskNote, LocalDate subTaskStartDate, LocalDate subTaskEndDate) {
        this.subTaskId = subTaskId;
        this.subTaskName = subTaskName;
        this.subTaskDescription = subTaskDescription;
        this.subTaskDuration = subTaskDuration;
        this.subTaskStatus = subTaskStatus;
        this.subTaskPriority = subTaskPriority;
        this.subTaskNote = subTaskNote;
        this.subTaskStartDate = subTaskStartDate;
        this.subTaskEndDate = subTaskEndDate;
    }

    public void recalculateDuration() {
        if (subTaskStartDate != null && subTaskEndDate != null) {
            long days = ChronoUnit.DAYS.between(subTaskStartDate, subTaskEndDate);

            // Hvis I vil tælle begge dage med:
            // days = days + 1;

            if (days < 0) {
                subTaskDuration = 0; // eller kast exception, hvis det er “ulovligt”
            } else {
                subTaskDuration = (int) days;
            }
        } else {
            subTaskDuration = 0;
        }
    }

    public long getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(long subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getSubTaskName() {
        return subTaskName;
    }

    public void setSubTaskName(String subTaskName) {
        this.subTaskName = subTaskName;
    }

    public String getSubTaskDescription() {
        return subTaskDescription;
    }

    public void setSubTaskDescription(String subTaskDescription) {
        this.subTaskDescription = subTaskDescription;
    }

    public int getSubTaskDuration() {
        return subTaskDuration;
    }

    public void setSubTaskDuration(int subTaskDuration) {
        this.subTaskDuration = subTaskDuration;
    }

    public Status getSubTaskStatus() {
        return subTaskStatus;
    }

    public void setSubTaskStatus(Status subTaskStatus) {
        this.subTaskStatus = subTaskStatus;
    }

    public Priority getSubTaskPriority() {
        return subTaskPriority;
    }

    public void setSubTaskPriority(Priority subTaskPriority) {
        this.subTaskPriority = subTaskPriority;
    }

    public LocalDate getSubTaskStartDate() {
        return subTaskStartDate;
    }

    public void setSubTaskStartDate(LocalDate subTaskStartDate) {
        this.subTaskStartDate = subTaskStartDate;
    }

    public LocalDate getSubTaskEndDate() {
        return subTaskEndDate;
    }

    public void setSubTaskEndDate(LocalDate subTaskEndDate) {
        this.subTaskEndDate = subTaskEndDate;
    }

    public String getSubTaskNote() {
        return subTaskNote;
    }

    public void setSubTaskNote(String subTaskNote) {
        this.subTaskNote = subTaskNote;
    }
}