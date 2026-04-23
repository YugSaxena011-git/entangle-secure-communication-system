package com.entangle.messaging.user.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String userId;
    private String username;
    private String displayName;
    private String password;
    private String sessionToken;
    private List<String> contacts = new ArrayList<>();
    private long createdAt;

    public User() {
        this.userId = "usr_" + UUID.randomUUID().toString().substring(0, 8);
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username.toLowerCase();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public List<String> getContacts() {
        return contacts;
    }

    public void addContact(String username) {
        if (!contacts.contains(username)) {
            contacts.add(username);
        }
    }

    public long getCreatedAt() {
        return createdAt;
    }
}