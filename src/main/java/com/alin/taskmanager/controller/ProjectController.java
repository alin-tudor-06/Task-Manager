package com.alin.taskmanager.controller;

import com.alin.taskmanager.dto.*;
import com.alin.taskmanager.security.CustomUserDetails;
import com.alin.taskmanager.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@Tag(name = "Projects",description = "Project management endpoints")
public class ProjectController {
    @Autowired
    ProjectService projectService;

    @Operation(summary = "Create new project")
    @PostMapping
    public ProjectResponse create(@AuthenticationPrincipal CustomUserDetails customUserDetails,@Valid @RequestBody ProjectCreate request){
        return projectService.create(customUserDetails.getUsername(), request);
    }

    @Operation(summary = "List my projects")
    @GetMapping
    public List<ProjectResponse> getAllForUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return projectService.getAllForUser(customUserDetails.getUsername());
    }

    @Operation(summary = "Get project by ID")
    @GetMapping("/{projectId}")
    public ProjectResponse findById(@AuthenticationPrincipal CustomUserDetails customUserDetails,@PathVariable Long projectId){
        return projectService.findById(projectId, customUserDetails.getUsername());
    }

    @Operation(summary = "Update project")
    @PutMapping("/{projectId}")
    public ProjectResponse update(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                  @PathVariable Long projectId,
                                  @Valid @RequestBody ProjectUpdate request){
        return projectService.update(projectId, customUserDetails.getUsername(), request);
    }

    @Operation(summary = "Delete project")
    @DeleteMapping("/{projectId}")
    public void delete(@AuthenticationPrincipal CustomUserDetails customUserDetails,@PathVariable Long projectId){
         projectService.delete(projectId, customUserDetails.getUsername());
    }

    @Operation(summary = "List project members")
    @GetMapping("/{projectId}/members")
    public List<ProjectMemberResponse> getAllMembers(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable Long projectId){
        return projectService.getAllMembers(projectId, customUserDetails.getUsername());
    }

    @Operation(summary = "Add member to project")
    @PostMapping("/{projectId}/members")
    public ProjectMemberResponse addMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable Long projectId,@Valid  @RequestBody ProjectMemberCreate request){
        return projectService.addMember(projectId, customUserDetails.getUsername(), request);
    }

    @Operation(summary = "Remove member from project")
    @DeleteMapping("/{projectId}/members/{targetUsername}")
    public void removeMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                             @PathVariable Long projectId,@PathVariable String targetUsername){
         projectService.removeMember(projectId, customUserDetails.getUsername(), targetUsername);
    }
}
