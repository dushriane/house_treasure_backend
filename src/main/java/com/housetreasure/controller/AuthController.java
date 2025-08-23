package com.housetreasure.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.User;
import com.housetreasure.repository.UserRepository;
import com.housetreasure.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class AuthController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //@Autowired
    public AuthController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        try {
            // Debug logging
            System.out.println("Registration request: " + request);
            
            // Extract data from request
            String username = (String) request.get("username");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            String firstName = (String) request.get("firstName");
            String lastName = (String) request.get("lastName");
            
            // Validate required fields
            if (email == null || password == null) {
                return ResponseEntity.badRequest().body("Email and password are required");
            }
            
            // Check if email exists
            if (userRepository.findByEmail(email) != null) {
                return ResponseEntity.badRequest().body("Email already in use");
            }
            
            // Create new user
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            
            userService.saveUser(user);
            
            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail()
                )
            ));
            
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
    
    // Add missing endpoints
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // For now, just return success (implement actual logout logic later)
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // For now, return a placeholder (implement actual user retrieval later)
        return ResponseEntity.ok(Map.of("message", "Current user endpoint"));
    }
    // public ResponseEntity<?> register(@RequestBody User user) {
    //     if (userRepository.findByEmail(user.getEmail()) != null) {
    //         return ResponseEntity.badRequest().body("Email already in use");
    //     }
    //     if (userRepository.findAll().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
    //         return ResponseEntity.badRequest().body("Username already in use");
    //     }
    //     user.setPassword(passwordEncoder.encode(user.getPassword()));
    //     userService.saveUser(user);
    //     return ResponseEntity.ok("User registered successfully");
    // }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody Map<String, String> credentials) {
    String email = credentials.get("email");
    String password = credentials.get("password");
    
    Optional<User> userOptional = userService.loginUser(email, password);
    
    return userOptional.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.status(401).build());
}

    public static class LoginRequest {
        private String email;
        private String password;
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
