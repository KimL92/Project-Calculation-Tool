package com.example.pkveksamen.controller;

import com.example.pkveksamen.model.*;
import com.example.pkveksamen.repository.TaskRepository;
import com.example.pkveksamen.service.EmployeeService;
import com.example.pkveksamen.service.ProjectService;
import com.example.pkveksamen.service.TaskService;
import jakarta.servlet.http.HttpSession;
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

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ProjectService projectService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private Model model;

    @InjectMocks
    private TaskController taskController;

    private Employee projectManager;
    private Employee teamMember;
    private Task testTask;

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

        testTask = new Task();
        testTask.setTaskID(1);
        testTask.setTaskName("Test Task");
        testTask.setTaskDescription("Test beskrivelse");
        testTask.setTaskStartDate(LocalDate.of(2025, 1, 1));
        testTask.setTaskDeadline(LocalDate.of(2025, 1, 31));
        testTask.setTaskStatus(Status.NOT_STARTED);
        testTask.setTaskPriority(Priority.MEDIUM);
    }

    @Test
    void isManager_WithProjectManager_ShouldReturnTrue() {
        // Act
        boolean result = taskController.isManager(projectManager);

        // Assert
        assertTrue(result);
    }

    @Test
    void isManager_WithTeamMember_ShouldReturnFalse() {
        // Act
        boolean result = taskController.isManager(teamMember);

        // Assert
        assertFalse(result);
    }


    @Test
    void showTaskByEmployeeId_AsManager_ShouldShowAllTasksInSubProject() {
        // Arrange
        List<Task> tasks = new ArrayList<>();
        tasks.add(testTask);

        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("seenTaskNotes")).thenReturn(null);

        when(employeeService.getEmployeeById(1)).thenReturn(projectManager);
        when(taskService.showTasksBySubProjectId(1L)).thenReturn(tasks);

        // Act
        String viewName = taskController.showTaskByEmployeeId(1, 1L, 1L, model, session);

        // Assert
        assertEquals("task", viewName);
        verify(taskService).showTasksBySubProjectId(1L);
        verify(model).addAttribute("taskList", tasks);
        verify(model).addAttribute("currentProjectId", 1L);
        verify(model).addAttribute("currentSubProjectId", 1L);
        verify(model).addAttribute("currentEmployeeId", 1);
    }



    @Test
    void showTaskByEmployeeId_AsTeamMember_ShouldShowOnlyAssignedTasks() {
        // Arrange
        List<Task> tasks = new ArrayList<>();
        tasks.add(testTask);

        // Configure Mocks
        when(employeeService.getEmployeeById(2)).thenReturn(teamMember);
        when(taskService.showTaskByEmployeeId(2)).thenReturn(tasks);
        HttpSession session = mock(HttpSession.class);

        // Act
        // Pass the mock session to the controller method
        String viewName = taskController.showTaskByEmployeeId(2, 1L, 1L, model, session);

        // Assert
        assertEquals("task", viewName);

        // Verify that the correct service method was called for a team member
        verify(taskService).showTaskByEmployeeId(2);

        // Verify that the manager-specific method was *not* called
        verify(taskService, never()).showTasksBySubProjectId(anyLong());

        // Verify model attributes were added
        verify(model).addAttribute("taskList", tasks);
        verify(model).addAttribute("currentProjectId", 1L);
        verify(model).addAttribute("currentSubProjectId", 1L);
        verify(model).addAttribute("currentEmployeeId", 2);

        // Verify that addEmployeeHeader was called (which is a void method we mocked as a no-op spy)
        // (If it were a standard mock, we would use verify(taskController).addEmployeeHeader(...) but it's an InjectMocks instance)
    }


    @Test
    void showTaskCreateForm_AsTeamMember_ShouldRedirect() {
        // Arrange
        when(employeeService.getEmployeeById(2)).thenReturn(teamMember);

        // Act
        String viewName = taskController.showTaskCreateForm(2, 1L, 1L, model);

        // Assert
        assertEquals("redirect:/project/task/liste/1/1/2", viewName);
        verify(projectService, never()).getProjectMembers(anyLong());
    }


    @Test
    void deleteTask_ShouldRedirectToTaskList() {
        // Act
        String viewName = taskController.deleteTask(1, 1L, 1L, 1L);

        // Assert
        assertEquals("redirect:/project/task/liste/1/1/1", viewName);
        verify(taskService).deleteTask(1L);
    }


}