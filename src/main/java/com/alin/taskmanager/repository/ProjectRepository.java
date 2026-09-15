package com.alin.taskmanager.repository;

import com.alin.taskmanager.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project,Long> {
    @Query("SELECT p FROM Project p JOIN p.projectMemberList pm WHERE pm.user.username = :username")
    List<Project> findAllByMemberUsername(@Param("username") String username);
}
