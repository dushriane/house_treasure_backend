package com.housetreasure.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.housetreasure.model.User;
import com.housetreasure.model.UserProfile;
import com.housetreasure.repository.UserProfileRepository;
import com.housetreasure.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, 
                      UserProfileRepository userProfileRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // === REGULAR USER ACTIVITIES ===
    
    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setIsActive(false); // Require email verification
        
        User savedUser = userRepository.save(user);
        
        // Create default profile
        UserProfile profile = new UserProfile();
        profile.setUser(savedUser);
        userProfileRepository.save(profile);
        
        return savedUser;
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            User u = user.get();
            u.setLastLoginAt(LocalDateTime.now());
            userRepository.save(u);
            return Optional.of(u);
        }
        return Optional.empty();
    }

    public boolean verifyEmail(String token) {
        Optional<User> user = userRepository.findByVerificationToken(token);
        if (user.isPresent()) {
            User u = user.get();
            u.setIsActive(true);
            u.setVerificationToken(null);
            userRepository.save(u);
            return true;
        }
        return false;
    }

    public String generatePasswordResetToken(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            String token = UUID.randomUUID().toString();
            User u = user.get();
            u.setVerificationToken(token); // Reuse field for reset token
            userRepository.save(u);
            return token;
        }
        return null;
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<User> user = userRepository.findByVerificationToken(token);
        if (user.isPresent()) {
            User u = user.get();
            u.setPassword(passwordEncoder.encode(newPassword));
            u.setVerificationToken(null);
            userRepository.save(u);
            return true;
        }
        return false;
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent() && passwordEncoder.matches(oldPassword, user.get().getPassword())) {
            User u = user.get();
            u.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(u);
            return true;
        }
        return false;
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.searchByName(name);
    }

    public List<User> searchUsersByLocation(String province, String district) {
        if (district != null) {
            return userRepository.findByProvinceAndDistrict(province, district);
        }
        return userRepository.findByProvince(province);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // === ADMIN ACTIVITIES ===
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Page<User> getAllUsersPageable(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public List<User> getActiveUsers() {
        return userRepository.findByIsActive(true);
    }

    public List<User> getInactiveUsers() {
        return userRepository.findByIsActive(false);
    }

    public User suspendUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User u = user.get();
            u.setIsActive(false);
            return userRepository.save(u);
        }
        return null;
    }

    public User unsuspendUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User u = user.get();
            u.setIsActive(true);
            return userRepository.save(u);
        }
        return null;
    }

    public long getTotalUsersCount() {
        return userRepository.count();
    }

    public long getActiveUsersCount() {
        return userRepository.countByIsActive(true);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
