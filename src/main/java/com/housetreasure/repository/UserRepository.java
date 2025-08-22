package com.housetreasure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.housetreasure.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByVerificationToken(String token);
    
    // Search users
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<User> searchByName(String name);
    
    List<User> findByProvinceAndDistrict(String province, String district);
    List<User> findByProvince(String province);
    
    // Admin queries
    List<User> findByIsActive(Boolean isActive);
    Page<User> findAll(Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN ?1 AND ?2")
    List<User> findUsersCreatedBetween(LocalDateTime start, LocalDateTime end);
    
    long countByIsActive(Boolean isActive);
}
