package com.housetreasure.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.User;
import com.housetreasure.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PostMapping
    public User saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }
}
