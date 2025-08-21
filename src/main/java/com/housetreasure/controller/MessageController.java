package com.housetreasure.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.Message;
import com.housetreasure.service.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<Message> getAllMessages() {
        return messageService.getAllMessages();
    }

    @PostMapping
    public Message createMessage(@RequestBody Message message) {
        return messageService.saveMessage(message);
    }
}
