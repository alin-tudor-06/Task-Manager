package com.alin.taskmanager.dto;

import com.alin.taskmanager.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdate {
    private String title;
    private String description;
    private Status status;
}
