package com.example.pkveksamen.Service;

import com.example.pkveksamen.Repository.TaskRepository;

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
