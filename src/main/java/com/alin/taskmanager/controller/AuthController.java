package com.alin.taskmanager.controller;

import com.alin.taskmanager.dto.JwtResponse;
import com.alin.taskmanager.dto.LoginRequest;
import com.alin.taskmanager.dto.UserResponse;
import com.alin.taskmanager.dto.RegisterRequest;
import com.alin.taskmanager.model.UserRole;
import com.alin.taskmanager.model.User;
import com.alin.taskmanager.repository.UserRepository;
import com.alin.taskmanager.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User registration and authentication")
public class AuthController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request){

        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new RuntimeException("Username already in use");
        }

        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already in use");
        }

        User user = new User(request.getUsername(), request.getEmail(),passwordEncoder.encode(request.getPassword()), UserRole.USER);
        userRepository.save(user);

        return new UserResponse(user.getUsername(),user.getEmail(),user.getCreatedAt());
    }

    @Operation(summary = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest request){
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));

            String token = jwtUtils.generateToken(authentication);

            String role = authentication.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).map(auth -> auth.replace("ROLE_","")).orElse("USER");

            String username = authentication.getName();
            return new JwtResponse(token,username,role);
        }
        catch (Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Username sau parola incorecte");
        }
    }
}