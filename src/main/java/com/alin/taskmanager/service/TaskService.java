package com.alin.taskmanager.service;

import com.alin.taskmanager.dto.TaskCreate;
import com.alin.taskmanager.dto.TaskResponse;
import com.alin.taskmanager.dto.TaskUpdate;
import com.alin.taskmanager.model.*;
import com.alin.taskmanager.repository.ProjectMemberRepository;
import com.alin.taskmanager.repository.ProjectRepository;
import com.alin.taskmanager.repository.TaskRepository;
import com.alin.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    private TaskResponse convertToDto(Task task){
        return new TaskResponse(task.getId(),task.getTitle(),task.getDescription(),task.getStatus(),task.getCreatedAt(),task.getUpdatedAt());
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Task findTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    private ProjectMember checkIsMemberOrThrow(String username, Long projectId) {
        User user = findUserOrThrow(username);
        return projectMemberRepository.findByUserIdAndProjectId(user.getId(), projectId)
                .orElseThrow(() -> new RuntimeException("User not a member of this project"));
    }

    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public TaskResponse create(Long projectId, String requesterUsername, TaskCreate request){
        Project project = findProjectOrThrow(projectId);
        User user = findUserOrThrow(requesterUsername);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,projectId);

        Task task = new Task(request.getTitle(),request.getDescription(), Status.TODO,project);
        taskRepository.save(task);
        return convertToDto(task);
    }

    public List<TaskResponse> getAllByProject(Long projectId, String requesterUsername){
        Project project = findProjectOrThrow(projectId);
        User user = findUserOrThrow(requesterUsername);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,projectId);

        return taskRepository.findAllByProjectId(projectId).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public TaskResponse getById(Long taskId,String requesterUsername){
        Task task = findTaskOrThrow(taskId);
        User user = findUserOrThrow(requesterUsername);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,task.getProject().getId());

        return convertToDto(task);
    }

    public TaskResponse update(Long taskId, String requesterUsername, TaskUpdate request){
        Task task = findTaskOrThrow(taskId);
        User user = findUserOrThrow(requesterUsername);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,task.getProject().getId());

        if(request.getTitle() != null){
            task.setTitle(request.getTitle());
        }

        if(request.getDescription() != null){
            task.setDescription(request.getDescription());
        }

        if(request.getStatus() != null){
            task.setStatus(request.getStatus());
        }

        taskRepository.save(task);
        return convertToDto(task);
    }

    public void delete(Long taskId,String requesterUsername){
        Task task = findTaskOrThrow(taskId);
        User user = findUserOrThrow(requesterUsername);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,task.getProject().getId());

        taskRepository.delete(task);
    }
}
