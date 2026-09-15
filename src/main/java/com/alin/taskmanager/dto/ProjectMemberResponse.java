package com.alin.taskmanager.dto;

import com.alin.taskmanager.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberResponse {
    private String username;
    private Role role;
    private LocalDateTime joinedAt;
}
