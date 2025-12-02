package com.example.pkveksamen.controller;

import com.example.pkveksamen.model.Employee;
import com.example.pkveksamen.model.Project;
import com.example.pkveksamen.model.SubProject;
import com.example.pkveksamen.model.Task;
import com.example.pkveksamen.service.EmployeeService;
import com.example.pkveksamen.service.ProjectService;
import com.example.pkveksamen.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("task")
public class TaskController {

    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    public TaskController (TaskService taskService, EmployeeService employeeService,ProjectService projectService ) {
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.projectService = projectService;
  }


    @GetMapping("/createtask/{projectId}/{subprojectId}")
    public String showTaskCreateForm(@PathVariable long subProjectId,
                                           @PathVariable long projectId ,
                                           Model model) {
        model.addAttribute("task", new Task());
        model.addAttribute("currentProjectId", projectId);
        model.addAttribute("currentSubProjectId", subProjectId);
        return "createtask";
    }

    @PostMapping("/createtask/{subprojectId}")
    public String createTask(@PathVariable Integer employeeId,
                                @PathVariable long subProjectId,
                                @ModelAttribute Task task,
                                Model model) {
        task.recalculateDuration();

        taskService.createTask(
                employeeId,
                subProjectId,
                task.getTaskName(),
                task.getTaskDescription(),
                task.getTaskStatus(),
                task.getStartDate(),
                task.getEndDate(),
                task.getTaskDuration(),
                task.getTaskPriority(),
                task.getTaskNote()
        );

        return "redirect:/subproject/list/" + employeeId + subProjectId;
    }

    @GetMapping("/task/list/{projectID}")
    public String showTaskByEmployeeId(@RequestParam("employeeId") int employeeId,
                                         @PathVariable long subProjectID,
                                         Model model) {
        List<Task> taskList = taskService.showTaskByEmployeeId(employeeId);
        model.addAttribute("taskList", taskList);
        model.addAttribute("currentSubProjectId", subProjectID);
        model.addAttribute("currentEmployeeId", employeeId);

        // Add employee details for the header
        Employee employee = employeeService.getEmployeeById(employeeId);
        if (employee != null) {
            model.addAttribute("username", employee.getUsername());
            model.addAttribute("employeeRole", employee.getRole());
        }

        return "task";
    }


}


