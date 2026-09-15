package com.alin.taskmanager.repository;

import com.alin.taskmanager.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember,Long> {
    List<ProjectMember> findAllByUserId(Long userId);
    List<ProjectMember> findAllByProjectId(Long projectId);
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);
    Optional<ProjectMember> findByUserIdAndProjectId(Long userId, Long projectId);
}
