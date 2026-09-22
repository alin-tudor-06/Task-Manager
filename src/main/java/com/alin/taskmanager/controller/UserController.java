package com.alin.taskmanager.controller;

import com.alin.taskmanager.dto.UserResponse;
import com.alin.taskmanager.dto.UserUpdate;
import com.alin.taskmanager.security.CustomUserDetails;
import com.alin.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users",description = "User profile management")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(summary = "Get current user profile")
    @GetMapping("/me")
    public UserResponse getCurrentUser(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        return userService.getByUsername(customUserDetails.getUsername());
    }

    @Operation(summary = "Update current user profile")
    @PutMapping("/me")
    public UserResponse updateUserData(@AuthenticationPrincipal CustomUserDetails customUserDetails,@Valid @RequestBody UserUpdate request){
        return userService.update(customUserDetails.getUsername(),request);
    }

}
