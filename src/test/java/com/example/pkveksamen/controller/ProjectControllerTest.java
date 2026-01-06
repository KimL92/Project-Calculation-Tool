package com.example.pkveksamen.controller;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.model.EmployeeRole;
import com.example.pkveksamen.model.Project;
import com.example.pkveksamen.model.SubProject;
import com.example.pkveksamen.service.EmployeeService;
import com.example.pkveksamen.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ProjectControllerTest {

    @Mock
    private ProjectService projectService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private Model model;

    @InjectMocks
    private ProjectController projectController;

    private Employee projectManager;
    private Employee teamMember;
    private Project testProject;
    private SubProject testSubProject;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        projectManager = new Employee();
        projectManager.setEmployeeId(1);
        projectManager.setUsername("projektleder");
        projectManager.setRole(EmployeeRole.PROJECT_MANAGER);

        teamMember = new Employee();
        teamMember.setEmployeeId(2);
        teamMember.setUsername("udvikler");
        teamMember.setRole(EmployeeRole.TEAM_MEMBER);

        testProject = new Project();
        testProject.setProjectID(1L);
        testProject.setProjectName("Test Projekt");
        testProject.setProjectDescription("Test beskrivelse");
        testProject.setProjectStartDate(LocalDate.of(2025, 1, 1));
        testProject.setProjectDeadline(LocalDate.of(2025, 12, 31));
        testProject.setProjectCustomer("Test Kunde");

        testSubProject = new SubProject();
        testSubProject.setSubProjectID(1L);
        testSubProject.setSubProjectName("Test SubProjekt");
    }

    @Test
    void showProjectsByEmployeeId_ShouldReturnProjectView() {
        List<Project> projects = new ArrayList<>();
        projects.add(testProject);

        when(projectService.showProjectsByEmployeeId(1)).thenReturn(projects);
        when(employeeService.getEmployeeById(1)).thenReturn(projectManager);

        String viewName = projectController.showProjectsByEmployeeId(1, model);

            assertEquals("project", viewName);
        verify(model).addAttribute("projectList", projects);
        verify(model).addAttribute("currentEmployeeId", 1);
        verify(model).addAttribute("username", "projektleder");
        verify(model).addAttribute("employeeRole", EmployeeRole.PROJECT_MANAGER);
    }

    @Test
    void showSubprojectByProjectId_ShouldReturnSubprojectView() {
        List<SubProject> subProjects = new ArrayList<>();
        subProjects.add(testSubProject);

        when(projectService.showSubProjectsByProjectId(1L)).thenReturn(subProjects);
        when(employeeService.getEmployeeById(1)).thenReturn(projectManager);

        String viewName = projectController.showSubprojectByProjectId(1, 1L, model);

        assertEquals("subproject", viewName);
        verify(model).addAttribute("subProjectList", subProjects);
        verify(model).addAttribute("currentProjectId", 1L);
        verify(model).addAttribute("currentEmployeeId", 1);
    }



    @Test
    void showCreateSubProjectForm_ShouldReturnCreateSubProjectView() {
        SubProject subProject = new SubProject();

        String viewName = projectController.showCreateSubProjectForm(1, 1L, subProject, model);

        assertEquals("createsubproject", viewName);
        verify(model).addAttribute(eq("subProject"), any(SubProject.class));
        verify(model).addAttribute("currentEmployeeId", 1);
        verify(model).addAttribute("currentProjectId", 1L);
    }



    @Test
    void showEditForm_ShouldReturnEditProjectView() {
        when(projectService.getProjectById(1L)).thenReturn(testProject);
        when(employeeService.getEmployeeById(1)).thenReturn(projectManager);

        String viewName = projectController.showEditForm(1, 1L, model);

        assertEquals("edit-project", viewName);
        verify(model).addAttribute("project", testProject);
        verify(model).addAttribute("currentEmployeeId", 1);
    }


    @Test
    void showProjectMembers_ShouldReturnViewProjectMembersView() {
        List<Employee> projectMembers = new ArrayList<>();
        projectMembers.add(teamMember);
        List<Employee> availableEmployees = new ArrayList<>();

        when(projectService.getProjectById(1L)).thenReturn(testProject);
        when(projectService.getProjectMembers(1L)).thenReturn(projectMembers);
        when(projectService.getAvailableEmployeesToAdd(1L)).thenReturn(availableEmployees);
        when(employeeService.getEmployeeById(1)).thenReturn(projectManager);

        String viewName = projectController.showProjectMembers(1, 1L, model);

        assertEquals("view-project-members", viewName);
        verify(model).addAttribute("project", testProject);
        verify(model).addAttribute("projectMembers", projectMembers);
        verify(model).addAttribute("availableEmployees", availableEmployees);
    }

    @Test
    void showAllEmployees_ShouldReturnViewAllEmployeesView() {
        List<Employee> employees = new ArrayList<>();
        employees.add(projectManager);
        employees.add(teamMember);

        when(employeeService.getAllEmployees()).thenReturn(employees);

        String viewName = projectController.showAllEmployees(1, model);

        assertEquals("view-all-employees", viewName);
        verify(model).addAttribute("employees", employees);
    }

    @Test
    void createProject_WithInvalidStartDateYear_ShouldReturnError() {
        testProject.setProjectStartDate(LocalDate.of(1999, 1, 1));

        String viewName = projectController.createProject(1, testProject, model);

        assertEquals("createproject", viewName);
        verify(model).addAttribute("error", "Start date year must be between 2000 and 2100");
        verify(projectService, never()).createProject(anyString(), anyString(), any(), any(), anyString(), anyInt());
    }

    @Test
    void createProject_WithInvalidDeadlineYear_ShouldReturnError() {
        testProject.setProjectStartDate(LocalDate.of(2025, 1, 1));
        testProject.setProjectDeadline(LocalDate.of(2101, 1, 1));

        String viewName = projectController.createProject(1, testProject, model);

        assertEquals("createproject", viewName);
        verify(model).addAttribute("error", "Deadline year must be between 2000 and 2100");
        verify(projectService, never()).createProject(anyString(), anyString(), any(), any(), anyString(), anyInt());
    }

    @Test
    void createProject_WithValidDates_ShouldRedirectToProjectList() {
        testProject.setProjectStartDate(LocalDate.of(2025, 1, 1));
        testProject.setProjectDeadline(LocalDate.of(2025, 12, 31));

        String viewName = projectController.createProject(1, testProject, model);

        assertEquals("redirect:/project/list/1", viewName);
        verify(projectService).createProject(
                testProject.getProjectName(),
                testProject.getProjectDescription(),
                testProject.getProjectStartDate(),
                testProject.getProjectDeadline(),
                testProject.getProjectCustomer(),
                1
        );
    }
}