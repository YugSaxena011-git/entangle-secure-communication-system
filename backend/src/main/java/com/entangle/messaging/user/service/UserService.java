package com.entangle.messaging.user.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.entangle.messaging.user.model.User;
import com.entangle.messaging.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String displayName, String password) {
        String cleanUsername = username.trim().toLowerCase();

        Optional<User> existing = userRepository.findByUsername(cleanUsername);

        if (existing.isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(cleanUsername);
        user.setDisplayName(displayName.trim());
        user.setPassword(passwordEncoder.encode(password.trim()));
        user.setSessionToken(UUID.randomUUID().toString());

        return userRepository.save(user);
    }

    public User login(String username, String password) {
        String cleanUsername = username.trim().toLowerCase();

        User user = userRepository.findByUsername(cleanUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password.trim(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        user.setSessionToken(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    public User restoreSession(String token) {
        return userRepository.findBySessionToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid session"));
    }

    public void logout(String token) {
        User user = userRepository.findBySessionToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid session"));

        user.setSessionToken(null);
        userRepository.save(user);
    }

    public User addContact(String username, String contactUsername) {
        String cleanUsername = username.trim().toLowerCase();
        String cleanContactUsername = contactUsername.trim().toLowerCase();

        if (cleanUsername.equals(cleanContactUsername)) {
            throw new RuntimeException("You cannot add yourself as a contact");
        }

        User user = userRepository.findByUsername(cleanUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User contact = userRepository.findByUsername(cleanContactUsername)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        user.addContact(contact.getUsername());

        return userRepository.save(user);
    }

    public List<String> getContacts(String username) {
        String cleanUsername = username.trim().toLowerCase();

        User user = userRepository.findByUsername(cleanUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getContacts();
    }
}