package com.housetreasure.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.User;
import com.housetreasure.model.UserProfile;
import com.housetreasure.service.UserProfileService;
import com.housetreasure.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class UserController {
    private final UserService userService;
    private final UserProfileService userProfileService;

    public UserController(UserService userService, UserProfileService userProfileService){
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    // === USER REGISTRATION & AUTH ===
    
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        Optional<User> user = userService.loginUser(email, password);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.status(401).build());
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        boolean verified = userService.verifyEmail(token);
        return verified ? ResponseEntity.ok("Email verified successfully") 
                       : ResponseEntity.badRequest().body("Invalid verification token");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String token = userService.generatePasswordResetToken(email);
        return token != null ? ResponseEntity.ok("Reset token generated") 
                            : ResponseEntity.badRequest().body("Email not found");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        boolean reset = userService.resetPassword(token, newPassword);
        return reset ? ResponseEntity.ok("Password reset successfully")
                    : ResponseEntity.badRequest().body("Invalid reset token");
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long id, 
                                                @RequestBody Map<String, String> request) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        boolean changed = userService.changePassword(id, oldPassword, newPassword);
        return changed ? ResponseEntity.ok("Password changed successfully")
                      : ResponseEntity.badRequest().body("Invalid old password");
    }

    // === USER SEARCH & PROFILE ===
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                         .map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String province,
                                 @RequestParam(required = false) String district) {
        if (name != null) {
            return userService.searchUsersByName(name);
        }
        if (province != null) {
            return userService.searchUsersByLocation(province, district);
        }
        return List.of();
    }

    // === USER PROFILE MANAGEMENT ===
    
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable Long id) {
        return userProfileService.getProfileByUserId(id)
                                .map(ResponseEntity::ok)
                                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfile> updateProfile(@PathVariable Long id, 
                                                    @RequestBody UserProfile profile) {
        UserProfile updated = userProfileService.updateProfile(profile);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/profile-picture")
    public ResponseEntity<UserProfile> updateProfilePicture(@PathVariable Long id,
                                                           @RequestBody Map<String, String> request) {
        String profilePictureUrl = request.get("profilePictureUrl");
        UserProfile updated = userProfileService.updateProfilePicture(id, profilePictureUrl);
        return updated != null ? ResponseEntity.ok(updated) 
                              : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<UserProfile> updatePreferences(@PathVariable Long id,
                                                        @RequestBody Map<String, Object> request) {
        String language = (String) request.get("language");
        Boolean emailNotifications = (Boolean) request.get("emailNotifications");
        
        UserProfile updated = userProfileService.updatePreferences(id, language, emailNotifications);
        return updated != null ? ResponseEntity.ok(updated) 
                              : ResponseEntity.notFound().build();
    }

    // === ADMIN ENDPOINTS ===
    
    @GetMapping("/admin/all")
    public Page<User> getAllUsers(Pageable pageable) {
        return userService.getAllUsersPageable(pageable);
    }

    @GetMapping("/admin/active")
    public List<User> getActiveUsers() {
        return userService.getActiveUsers();
    }

    @GetMapping("/admin/inactive")
    public List<User> getInactiveUsers() {
        return userService.getInactiveUsers();
    }

    @PutMapping("/admin/{id}/suspend")
    public ResponseEntity<User> suspendUser(@PathVariable Long id) {
        User suspended = userService.suspendUser(id);
        return suspended != null ? ResponseEntity.ok(suspended) 
                                : ResponseEntity.notFound().build();
    }

    @PutMapping("/admin/{id}/unsuspend")
    public ResponseEntity<User> unsuspendUser(@PathVariable Long id) {
        User unsuspended = userService.unsuspendUser(id);
        return unsuspended != null ? ResponseEntity.ok(unsuspended) 
                                   : ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/stats")
    public Map<String, Long> getUserStats() {
        return Map.of(
            "totalUsers", userService.getTotalUsersCount(),
            "activeUsers", userService.getActiveUsersCount()
        );
    }

    // Legacy endpoints (keep for backward compatibility)
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
}
