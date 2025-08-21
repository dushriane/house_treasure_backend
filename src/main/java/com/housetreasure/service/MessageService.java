package com.housetreasure.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.housetreasure.model.Message;
import com.housetreasure.repository.MessageRepository;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}
