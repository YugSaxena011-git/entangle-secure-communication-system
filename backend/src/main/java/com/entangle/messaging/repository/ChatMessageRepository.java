package com.entangle.messaging.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.entangle.messaging.model.ChatMessageEntity;

public interface ChatMessageRepository extends MongoRepository<ChatMessageEntity, String> {

    List<ChatMessageEntity> findByRoomIdOrderByTimestampAsc(String roomId);
}