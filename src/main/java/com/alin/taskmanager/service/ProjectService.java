package com.alin.taskmanager.service;

import com.alin.taskmanager.dto.*;
import com.alin.taskmanager.model.Project;
import com.alin.taskmanager.model.ProjectMember;
import com.alin.taskmanager.model.ProjectRole;
import com.alin.taskmanager.model.User;
import com.alin.taskmanager.repository.ProjectMemberRepository;
import com.alin.taskmanager.repository.ProjectRepository;
import com.alin.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;
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
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Project findProjectOrThrow(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    private ProjectMember checkIsMemberOrThrow(String username, Long projectId) {
        User user = findUserOrThrow(username);
        return projectMemberRepository.findByUserIdAndProjectId(user.getId(), projectId)
                .orElseThrow(() -> new RuntimeException("User not a member of this project"));
    }

    private ProjectMember checkIsOwnerOrThrow(String username, Long projectId) {
        ProjectMember pm = checkIsMemberOrThrow(username, projectId);
        if (pm.getProjectRole() != ProjectRole.OWNER) {
            throw new RuntimeException("Only the owner can do this");
        }
        return pm;
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
        User user = findUserOrThrow(requesterUsername);
        Project project = findProjectOrThrow(projectId);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,projectId);

        return convertToDto(project);
    }

    public ProjectResponse update(Long projectId, String requesterUsername, ProjectUpdate request){
        User user = findUserOrThrow(requesterUsername);
        Project project = findProjectOrThrow(projectId);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,projectId);

        if(pm.getProjectRole().equals(ProjectRole.OWNER)){
            if(request.getTitle() != null){
                project.setTitle(request.getTitle());
            }
            if(request.getDescription() != null){
                project.setDescription(request.getDescription());
            }
            projectRepository.save(project);
            return convertToDto(project);
        }
        else throw new AccessDeniedException("User does not have permission to update this project");
    }

    public void delete(Long projectId,String requesterUsername){
        User user = findUserOrThrow(requesterUsername);
        Project project = findProjectOrThrow(projectId);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,projectId);

        if(pm.getProjectRole().equals(ProjectRole.OWNER)){
            projectRepository.delete(project);
        }
        else throw new AccessDeniedException("User does not have permission to delete this project");
    }

    public List<ProjectMemberResponse> getAllMembers(Long projectId,String requesterUsername){
        User user = findUserOrThrow(requesterUsername);
        Project project = findProjectOrThrow(projectId);

        if(projectMemberRepository.existsByUserIdAndProjectId(user.getId(),projectId)){
            return projectMemberRepository.findAllByProjectId(projectId).stream().map(this::convertToDto).collect(Collectors.toList());
        }
        else throw new AccessDeniedException("User can't view this project's members because he is not a member of it");
    }

    public ProjectMemberResponse addMember(Long projectId, String requesterUsername, ProjectMemberCreate request){
        User user = findUserOrThrow(requesterUsername);
        Project project = findProjectOrThrow(projectId);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,projectId);

        if(pm.getProjectRole().equals(ProjectRole.OWNER)){
            User addedUser = userRepository.findByUsername(request.getUsername()).orElseThrow(()-> new RuntimeException("The user you are trying to add does not exist"));

            if(projectMemberRepository.existsByUserIdAndProjectId(addedUser.getId(),projectId)){
                throw new RuntimeException("The user you are trying to add is already a member of this project");
            }

            ProjectMember addedPm = new ProjectMember(ProjectRole.MEMBER,addedUser,project);
            projectMemberRepository.save(addedPm);
            return convertToDto(addedPm);
        }
        else throw new AccessDeniedException("User does not have permission to add a member");
    }

    public void removeMember(Long projectId,String requesterUsername,String targetUsername){
        if(targetUsername.equals(requesterUsername)){
            throw new RuntimeException("The owner can't delete himself");
        }

        User user = findUserOrThrow(requesterUsername);
        Project project = findProjectOrThrow(projectId);
        ProjectMember pm = checkIsMemberOrThrow(requesterUsername,projectId);

        if(pm.getProjectRole().equals(ProjectRole.OWNER)){
            User removedUser = userRepository.findByUsername(targetUsername).orElseThrow(()-> new RuntimeException("The user you are trying to remove does not exist"));
            ProjectMember removedPm = projectMemberRepository.findByUserIdAndProjectId(removedUser.getId(),projectId).orElseThrow(()-> new RuntimeException("The user you are trying to remove is not a member of this project"));
            projectMemberRepository.delete(removedPm);
        }

        else throw new AccessDeniedException("The user does not have permission to remove a member");
    }
}
