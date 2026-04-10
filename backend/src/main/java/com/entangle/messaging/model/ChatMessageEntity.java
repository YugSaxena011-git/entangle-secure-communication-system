package com.entangle.messaging.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "messages")
public class ChatMessageEntity {

    @Id
    private String id;

    private String sender;
    private String content;
    private String roomId;
    private String timestamp;
    private String type;
    private String integrityStatus;

    public ChatMessageEntity() {
    }

    public ChatMessageEntity(String sender, String content, String roomId,
                             String timestamp, String type, String integrityStatus) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
        this.timestamp = timestamp;
        this.type = type;
        this.integrityStatus = integrityStatus;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public String getIntegrityStatus() {
        return integrityStatus;
    }
}