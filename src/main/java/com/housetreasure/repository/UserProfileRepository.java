package com.housetreasure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.housetreasure.model.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
}
