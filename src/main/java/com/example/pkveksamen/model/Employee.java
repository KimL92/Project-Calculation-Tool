package com.example.pkveksamen.model;

import java.util.List;

public class Employee {
    private int employeeId;
    private String username;
    private String password;
    private String email;
    private EmployeeRole role; // TEXT type in SQL
    private AlphaRole skill;

    public Employee() {
    }

    public Employee(String username, String password, String email, EmployeeRole role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmployeeRole getRole() {
        return role;
    }

    public void setRole(EmployeeRole role) {
        this.role = role;
    }

    public AlphaRole getSkill() {
        return skill;
    }

    public void setSkill(AlphaRole skill) {
        this.skill = skill;
    }
}
