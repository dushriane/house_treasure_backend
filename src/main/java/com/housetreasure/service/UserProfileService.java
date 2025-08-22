package com.housetreasure.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.housetreasure.model.UserProfile;
import com.housetreasure.repository.UserProfileRepository;

@Service
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    public Optional<UserProfile> getProfileByUserId(Long userId) {
        return userProfileRepository.findByUserId(userId);
    }

    public UserProfile updateProfile(UserProfile profile) {
        profile.setUpdatedAt(LocalDateTime.now());
        return userProfileRepository.save(profile);
    }

    public UserProfile updateProfilePicture(Long userId, String profilePictureUrl) {
        Optional<UserProfile> profile = userProfileRepository.findByUserId(userId);
        if (profile.isPresent()) {
            UserProfile p = profile.get();
            p.setProfilePictureUrl(profilePictureUrl);
            p.setUpdatedAt(LocalDateTime.now());
            return userProfileRepository.save(p);
        }
        return null;
    }

    public UserProfile updatePreferences(Long userId, String language, Boolean emailNotifications) {
        Optional<UserProfile> profile = userProfileRepository.findByUserId(userId);
        if (profile.isPresent()) {
            UserProfile p = profile.get();
            p.setPreferredLanguage(language);
            p.setEmailNotifications(emailNotifications);
            p.setUpdatedAt(LocalDateTime.now());
            return userProfileRepository.save(p);
        }
        return null;
    }

    public UserProfile incrementItemsListed(Long userId) {
        Optional<UserProfile> profile = userProfileRepository.findByUserId(userId);
        if (profile.isPresent()) {
            UserProfile p = profile.get();
            p.setItemsListed(p.getItemsListed() + 1);
            return userProfileRepository.save(p);
        }
        return null;
    }

    public UserProfile incrementItemsSold(Long userId) {
        Optional<UserProfile> profile = userProfileRepository.findByUserId(userId);
        if (profile.isPresent()) {
            UserProfile p = profile.get();
            p.setItemsSold(p.getItemsSold() + 1);
            return userProfileRepository.save(p);
        }
        return null;
    }

    public UserProfile incrementItemsPurchased(Long userId) {
        Optional<UserProfile> profile = userProfileRepository.findByUserId(userId);
        if (profile.isPresent()) {
            UserProfile p = profile.get();
            p.setItemsPurchased(p.getItemsPurchased() + 1);
            return userProfileRepository.save(p);
        }
        return null;
    }

    public void updateLastActive(Long userId) {
        userProfileRepository.findByUserId(userId).ifPresent(profile -> {
            profile.setLastActiveAt(LocalDateTime.now());
            userProfileRepository.save(profile);
        });
    }
}
