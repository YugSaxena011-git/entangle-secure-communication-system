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
    private Long createdAtEpoch;
    private String type;
    private String integrityStatus;
    private boolean oneTime;
    private boolean consumed;

    public ChatMessageEntity() {
    }

    public ChatMessageEntity(String sender, String content, String roomId,
                             String timestamp, Long createdAtEpoch, String type,
                             String integrityStatus, boolean oneTime, boolean consumed) {
        this.sender = sender;
        this.content = content;
        this.roomId = roomId;
        this.timestamp = timestamp;
        this.createdAtEpoch = createdAtEpoch;
        this.type = type;
        this.integrityStatus = integrityStatus;
        this.oneTime = oneTime;
        this.consumed = consumed;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Long getCreatedAtEpoch() {
        return createdAtEpoch;
    }

    public String getType() {
        return type;
    }

    public String getIntegrityStatus() {
        return integrityStatus;
    }

    public boolean isOneTime() {
        return oneTime;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public boolean isConsumed() {
        return consumed;
    }
}