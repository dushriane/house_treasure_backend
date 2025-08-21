package com.housetreasure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.housetreasure.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
