package com.alin.taskmanager.controller;

import com.alin.taskmanager.dto.TaskCreate;
import com.alin.taskmanager.dto.TaskResponse;
import com.alin.taskmanager.dto.TaskUpdate;
import com.alin.taskmanager.security.CustomUserDetails;
import com.alin.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Tasks",description = "Task management endpoints")
public class TaskController {
    @Autowired
    TaskService taskService;

    @Operation(summary = "Create a task in a project")
    @PostMapping("/projects/{projectId}/tasks")
    public TaskResponse create(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                               @PathVariable Long projectId,
                               @Valid @RequestBody TaskCreate request){
        return taskService.create(projectId, customUserDetails.getUsername(), request);
    }

    @Operation(summary = "List tasks in a project")
    @GetMapping("/projects/{projectId}/tasks")
    public List<TaskResponse>  getAllByProject(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                @PathVariable Long projectId){
        return taskService.getAllByProject(projectId, customUserDetails.getUsername());
    }

    @Operation(summary = "Get task by ID")
    @GetMapping("/tasks/{taskId}")
    public TaskResponse getById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                   @PathVariable Long taskId){
        return taskService.getById(taskId, customUserDetails.getUsername());
    }

    @Operation(summary = "Update task")
    @PutMapping("/tasks/{taskId}")
    public TaskResponse update(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                               @PathVariable Long taskId,
                               @Valid @RequestBody TaskUpdate request){
        return taskService.update(taskId, customUserDetails.getUsername(), request);
    }

    @Operation(summary = "Delete task")
    @DeleteMapping("/tasks/{taskId}")
    public void delete(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                               @PathVariable Long taskId){
         taskService.delete(taskId, customUserDetails.getUsername());
    }
}
