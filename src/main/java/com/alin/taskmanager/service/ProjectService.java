package com.alin.taskmanager.service;

import com.alin.taskmanager.dto.*;
import com.alin.taskmanager.exception.BadRequestException;
import com.alin.taskmanager.exception.ConflictException;
import com.alin.taskmanager.exception.ForbiddenException;
import com.alin.taskmanager.exception.NotFoundException;
import com.alin.taskmanager.model.Project;
import com.alin.taskmanager.model.ProjectMember;
import com.alin.taskmanager.model.ProjectRole;
import com.alin.taskmanager.model.User;
import com.alin.taskmanager.repository.ProjectMemberRepository;
import com.alin.taskmanager.repository.ProjectRepository;
import com.alin.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    private ProjectResponse convertToDto(Project project){
        return new ProjectResponse(project.getId(), project.getTitle(), project.getDescription(), project.getCreatedAt());
    }

    private ProjectMemberResponse convertToDto(ProjectMember projectMember){
        return new ProjectMemberResponse(projectMember.getUser().getUsername(),projectMember.getProjectRole(),projectMember.getJoinedAt());
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));
    }

    private void checkIsMemberOrThrow(String username, Long projectId) {
        User user = findUserOrThrow(username);
         projectMemberRepository.findByUserIdAndProjectId(user.getId(), projectId)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this project"));
    }

    private void checkIsOwnerOrThrow(String username, Long projectId) {
        User user = findUserOrThrow(username);
        ProjectMember pm = projectMemberRepository.findByUserIdAndProjectId(user.getId(), projectId)
                .orElseThrow(() -> new ForbiddenException("You are not a member of this project"));
        if (pm.getProjectRole() != ProjectRole.OWNER) {
            throw new ForbiddenException("Only the owner can perform this action");
        }
    }

    public ProjectResponse create(String ownerUsername, ProjectCreate request){
        User user = findUserOrThrow(ownerUsername);

        Project project = new Project(request.getTitle(),request.getDescription());
        projectRepository.save(project);

        ProjectMember pm = new ProjectMember(ProjectRole.OWNER,user,project);
        projectMemberRepository.save(pm);

        return convertToDto(project);
    }

    public List<ProjectResponse> getAllForUser(String username){
        return projectRepository.findAllByMemberUsername(username).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ProjectResponse findById(Long projectId,String requesterUsername){
        Project project = findProjectOrThrow(projectId);
        checkIsMemberOrThrow(requesterUsername,projectId);

        return convertToDto(project);
    }

    public ProjectResponse update(Long projectId, String requesterUsername, ProjectUpdate request){
        Project project = findProjectOrThrow(projectId);
        checkIsOwnerOrThrow(requesterUsername,projectId);


        if(request.getTitle() != null){
                project.setTitle(request.getTitle());
        }
        if(request.getDescription() != null){
                project.setDescription(request.getDescription());
        }
        projectRepository.save(project);
        return convertToDto(project);
    }

    public void delete(Long projectId,String requesterUsername){
        Project project = findProjectOrThrow(projectId);
        checkIsOwnerOrThrow(requesterUsername,projectId);

        projectRepository.delete(project);
    }

    public List<ProjectMemberResponse> getAllMembers(Long projectId,String requesterUsername){
        checkIsMemberOrThrow(requesterUsername,projectId);

        return projectMemberRepository.findAllByProjectId(projectId).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public ProjectMemberResponse addMember(Long projectId, String requesterUsername, ProjectMemberCreate request){
        Project project = findProjectOrThrow(projectId);
        checkIsOwnerOrThrow(requesterUsername,projectId);

        User addedUser = findUserOrThrow(request.getUsername());

        if(projectMemberRepository.existsByUserIdAndProjectId(addedUser.getId(),projectId)){
            throw new ConflictException("The user you are trying to add is already a member of this project");
        }

        ProjectMember addedPm = new ProjectMember(ProjectRole.MEMBER,addedUser,project);
        projectMemberRepository.save(addedPm);
        return convertToDto(addedPm);
    }

    public void removeMember(Long projectId,String requesterUsername,String targetUsername){
        if(targetUsername.equals(requesterUsername)){
            throw new BadRequestException("The owner cannot remove himself from the project");
        }

        checkIsOwnerOrThrow(requesterUsername, projectId);

        User removedUser = findUserOrThrow(targetUsername);
        ProjectMember removedPm = projectMemberRepository
                .findByUserIdAndProjectId(removedUser.getId(), projectId)
                .orElseThrow(() -> new NotFoundException("User is not a member of this project"));

        projectMemberRepository.delete(removedPm);
    }
}
