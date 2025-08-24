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
                return ResponseEntity.badRequest().body(Map.of("message","Email and password are required"));
            }
            
            // Check if email exists
            if (userRepository.findByEmail(email) != null) {
                return ResponseEntity.badRequest().body(Map.of("message","Email already in use"));
            }
            
            // Create new user
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);

            user.setPhoneNumber((String) request.get("phoneNumber"));
            user.setProvince((String) request.get("province"));
            user.setDistrict((String) request.get("district"));
            user.setSector((String) request.get("sector"));
            user.setCell((String) request.get("cell"));
            user.setVillage((String) request.get("village"));
            user.setMtnMobileMoneyNumber((String) request.get("mtnMobileMoneyNumber"));
            user.setAirtelMoneyNumber((String) request.get("airtelMoneyNumber"));
            user.setPreferredPaymentMethod((String) request.get("preferredPaymentMethod"));

            User savedUser = userService.saveUser(user);
            
            // Generate a simple token (replace with proper JWT)
            String token = "temp_token_" + savedUser.getId() + "_" + System.currentTimeMillis();

            return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "user", Map.of(
                    "id", savedUser.getId(),
                    "username", savedUser.getUsername(),
                    "email", savedUser.getEmail(),
                    "firstName", savedUser.getFirstName(),
                    "lastName", savedUser.getLastName()
                ),
                "token", token
            ));
            
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", "Registration failed: " + e.getMessage()));
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

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
    try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            
            System.out.println("Login attempt for email: " + email);
            
            Optional<User> userOptional = userService.loginUser(email, password);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String token = "temp_token_" + user.getId() + "_" + System.currentTimeMillis();
                
                return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "user", Map.of(
                        "id", user.getId(),
                        "username", user.getUsername(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName()
                    ),
                    "token", token
                ));
            } else {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Login failed: " + e.getMessage()));
        }
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
