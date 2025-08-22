package com.housetreasure.repository;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.housetreasure.model.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
    // Define any custom query methods if needed
}