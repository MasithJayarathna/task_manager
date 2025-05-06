package com.assignment.task.model.dto;

import com.assignment.task.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Date dueDate;
    private Long userId; // Or optionally just the username
    private String username;

    public TaskResponseDTO(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.dueDate = task.getDueDate();
        if (task.getUser() != null) {
            this.userId = task.getUser().getId();
            this.username = task.getUser().getUsername();
        }
    }
}
