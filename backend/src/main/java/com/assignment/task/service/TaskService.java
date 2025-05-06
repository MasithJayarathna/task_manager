package com.assignment.task.service;

import com.assignment.task.model.Task;
import com.assignment.task.model.User;
import com.assignment.task.model.dto.TaskResponseDTO;
import com.assignment.task.repository.TaskRepository;
import com.assignment.task.util.KeyWords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<TaskResponseDTO> getTasksForUser(User user) {
        List<Task> tasks = taskRepository.findByUser(user);
        return tasks.stream()
                .map(TaskResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Task createTask(Task task, User user) {
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Optional<Task> updateTaskForUser(Long id, Task newTask, User user) {
        return taskRepository.findByIdAndUser(id, user).map(task -> {
            task.setTitle(newTask.getTitle());
            task.setDescription(newTask.getDescription());
            task.setDueDate(newTask.getDueDate());
            return taskRepository.save(task);
        });
    }

    public String deleteTaskForUser(Long id, User user) {
        Optional<Task> task = taskRepository.findByIdAndUser(id, user);
        if (task.isPresent()) {
            taskRepository.delete(task.get());
            return KeyWords.SUCCESSFUL.name();
        }
        return KeyWords.NON_EXIST.name();
    }

}