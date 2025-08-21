package com.housetreasure.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.housetreasure.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // Define any custom query methods if needed
}