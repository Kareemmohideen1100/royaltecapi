package com.royaltech.tasktec.controller;

import com.royaltech.tasktec.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.royaltech.tasktec.entity.userLogin;
import com.royaltech.tasktec.repository.userLoginRepository;

import java.util.Collections;

@RestController
@RequestMapping("/api/user")
public class loginController {
    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(loginController.class);

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private userLoginRepository userLoginRepository;

    // API to create a user
    @PostMapping("/create")
    public String createUser(@RequestBody userLogin user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userLoginRepository.save(user);
        return "User saved successfully!";
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody userLogin loginRequest) {
        try {
            logger.info("Login attempt for user: {}", loginRequest.getUsername());

            return userLoginRepository.findByUsername(loginRequest.getUsername())
                    .map(user -> {
                        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                            String token = jwtUtil.generateToken(user.getUsername());
                            return ResponseEntity.ok(Collections.singletonMap("token", token));
                        } else {
                            logger.warn("Invalid password for user: {}", loginRequest.getUsername());
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
                        }
                    })
                    .orElseGet(() -> {
                        logger.warn("User not found: {}", loginRequest.getUsername());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
                    });

        } catch (Exception e) {
            logger.error("Exception durdfedeing login for user {}: {}", loginRequest.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }



    // API to fetch a user by username
    @GetMapping("/{username}")
    public userLogin getUserByUsername(@PathVariable String username) {
        return userLoginRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
