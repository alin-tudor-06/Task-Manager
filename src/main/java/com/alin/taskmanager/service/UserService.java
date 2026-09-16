package com.alin.taskmanager.service;

import com.alin.taskmanager.dto.UserResponse;
import com.alin.taskmanager.dto.UserUpdate;
import com.alin.taskmanager.model.User;
import com.alin.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private UserResponse convertToDto(User user){
        return new UserResponse(user.getUsername(), user.getEmail(), user.getCreatedAt());
    }

    public UserResponse getByUsername(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Not found"));
        return convertToDto(user);
    }

    public UserResponse update(String username,UserUpdate request){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Not found"));

        if(request.getEmail() != null){
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);
        return convertToDto(user);
    }
}
