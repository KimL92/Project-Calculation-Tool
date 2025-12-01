package com.example.pkveksamen.Model;

import java.time.LocalDate;

public class Task {
    private int taskID;
    private String taskName;
    private String taskDescription;
    private String taskStatus;
    private String duration;
    private String taskNote;
    private LocalDate startDate;
    private LocalDate endDate;
    private long subTaskID;
    private String subTaskName;
    private String subTaskDescription;
    private String subTaskDuration;

    public Task(){}

    public Task(int taskID, String taskName, String taskDescription, String taskStatus, String duration,
                String taskNote, LocalDate startDate, LocalDate endDate, long subTaskID,
                String subTaskName, String subTaskDescription, String subTaskDuration) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.duration = duration;
        this.taskNote = taskNote;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subTaskID = subTaskID;
        this.subTaskName = subTaskName;
        this.subTaskDescription = subTaskDescription;
        this.subTaskDuration = subTaskDuration;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTaskNote() {
        return taskNote;
    }

    public void setTaskNote(String taskNote) {
        this.taskNote = taskNote;
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

    public long getSubTaskID() {
        return subTaskID;
    }

    public void setSubTaskID(long subTaskID) {
        this.subTaskID = subTaskID;
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

    public String getSubTaskDuration() {
        return subTaskDuration;
    }

    public void setSubTaskDuration(String subTaskDuration) {
        this.subTaskDuration = subTaskDuration;
    }
}
