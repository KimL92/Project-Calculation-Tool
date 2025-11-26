package com.aljamour.pkveksamen.Service;

import com.aljamour.pkveksamen.Model.Task;
import com.aljamour.pkveksamen.Repository.ProjectRepository;
import com.aljamour.pkveksamen.Repository.TaskRepository;

import java.util.List;

public class TaskService {

    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}


//    public List<Task> getTasksByTaskID(int taskID) {
//        return taskRepository.getTasksByTaskID(taskID);
//    }
//}
