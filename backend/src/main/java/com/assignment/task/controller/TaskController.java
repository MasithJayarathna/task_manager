package com.assignment.task.controller;

import com.assignment.task.model.Task;
import com.assignment.task.model.dto.TaskResponseDTO;
import com.assignment.task.service.TaskService;
import com.assignment.task.service.UserService;
import com.assignment.task.util.KeyWords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired private TaskService taskService;

    @Autowired private UserService userService;



    @GetMapping
    public ResponseEntity<?> getTasks(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request to get Tasks");
        try{
            List<TaskResponseDTO> res = taskService.getTasksForUser(userService.getCurrentUser(userDetails));
            return ResponseEntity.ok(res);
        }catch (Exception e){
            log.error("An error occurred while retrieving tasks {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred at the server side.");
        }

    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request to create a new task");
        try{
            Task res = taskService.createTask(task, userService.getCurrentUser(userDetails));
            TaskResponseDTO dto = new TaskResponseDTO(res);
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        }catch (Exception e){
            log.error("An error occurred while creating tasks {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred at the server side.");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task updatedTask,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Request to update a task");
        try{
            Optional<Task> res = taskService.updateTaskForUser(id, updatedTask, userService.getCurrentUser(userDetails));
            if(res.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
            }
            TaskResponseDTO dto = new TaskResponseDTO(res.get());
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }catch (Exception e){
            log.error("An error occurred while updating tasks {}", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred at the server side.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Request to delete a task");
        try {
            String deleted = taskService.deleteTaskForUser(id, userService.getCurrentUser(userDetails));
            if(deleted.equals(KeyWords.NON_EXIST.name())){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        }catch (Exception e){
            log.error("An error occurred while deleting tasks {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred at the server side.");
        }
    }
}
