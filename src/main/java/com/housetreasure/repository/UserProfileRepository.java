package com.housetreasure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.housetreasure.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserId(Long userId);
    Optional<UserProfile> findByUser_Email(String email);
}
