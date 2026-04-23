package com.entangle.messaging.user.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.entangle.messaging.user.model.User;
import com.entangle.messaging.user.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public User register(@RequestParam String username,
                         @RequestParam String displayName,
                         @RequestParam String password) {

        return userService.register(username, displayName, password);
    }

    @GetMapping("/login")
    public User login(@RequestParam String username,
                      @RequestParam String password) {

        return userService.login(username, password);
    }

    @GetMapping("/restore")
    public User restore(@RequestParam String token) {
        return userService.restoreSession(token);
    }

    @GetMapping("/logout")
    public void logout(@RequestParam String token) {
        userService.logout(token);
    }

    @GetMapping("/addContact")
    public User addContact(@RequestParam String username,
                           @RequestParam String contactUsername) {

        return userService.addContact(username, contactUsername);
    }

    @GetMapping("/contacts")
    public List<String> getContacts(@RequestParam String username) {
        return userService.getContacts(username);
    }
}