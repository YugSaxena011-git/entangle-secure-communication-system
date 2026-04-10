package com.entangle.messaging.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.entangle.messaging.model.ChatMessage;
import com.entangle.messaging.model.ChatMessageEntity;
import com.entangle.messaging.repository.ChatMessageRepository;
import com.entangle.messaging.service.RoomService;
import com.entangle.messaging.stats.ChatStatsService;

@Controller
public class MessageController {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final ChatStatsService chatStatsService;
    private final ChatMessageRepository chatMessageRepository;
    private final RoomService roomService;

    public MessageController(ChatStatsService chatStatsService,
                             ChatMessageRepository chatMessageRepository,
                             RoomService roomService) {
        this.chatStatsService = chatStatsService;
        this.chatMessageRepository = chatMessageRepository;
        this.roomService = roomService;
    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {

        if (message == null) {
            return null;
        }

        String sender = safeValue(message.getSender(), "Anonymous");
        String content = safeTrim(message.getContent());
        String roomId = safeValue(message.getRoomId(), "general");
        String secretKey = safeTrim(message.getSecretKey());

        RoomService.AccessResult accessResult = roomService.validateMessageAccess(roomId, sender, secretKey);

        if (accessResult != RoomService.AccessResult.ALLOWED) {
            return buildSystemMessage(roomId, sender,
                    "ACCESS DENIED: You are not an authenticated member of room [" + roomId + "]",
                    "DENIED");
        }

        if (content.isEmpty()) {
            return null;
        }

        message.setSender(sender);
        message.setContent(content);
        message.setRoomId(roomId);
        message.setTimestamp(LocalTime.now().format(formatter));

        if (message.getType() == ChatMessage.MessageType.PRIORITY) {
            message.setType(ChatMessage.MessageType.PRIORITY);
        } else {
            message.setType(ChatMessage.MessageType.CHAT);
        }

        String serverHashInput = buildIntegrityBase(
                message.getSender(),
                message.getContent(),
                message.getRoomId(),
                message.getType().name(),
                secretKey
        );

        String serverHash = sha256(serverHashInput);
        String clientHash = safeTrim(message.getIntegrityHash());

        if (!secretKey.isEmpty() && clientHash.equals(serverHash)) {
            message.setIntegrityStatus("VERIFIED");
        } else {
            message.setIntegrityStatus("TAMPERED");
            roomService.registerTamperEvent(roomId);
        }

        message.setIntegrityHash(serverHash);
        message.setSecretKey(null);

        chatStatsService.recordMessage(message);

        chatMessageRepository.save(
                new ChatMessageEntity(
                        message.getSender(),
                        message.getContent(),
                        message.getRoomId(),
                        message.getTimestamp(),
                        message.getType().name(),
                        message.getIntegrityStatus()
                )
        );

        return message;
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/messages")
    public ChatMessage addUser(ChatMessage message) {

        if (message == null) {
            return null;
        }

        String sender = safeValue(message.getSender(), "Anonymous");
        String roomId = safeValue(message.getRoomId(), "general");
        String secretKey = safeTrim(message.getSecretKey());

        if (message.getType() == ChatMessage.MessageType.LEAVE) {
            roomService.leaveRoom(roomId, sender);

            message.setSender(sender);
            message.setRoomId(roomId);
            message.setTimestamp(LocalTime.now().format(formatter));
            message.setIntegrityHash(null);
            message.setSecretKey(null);
            message.setIntegrityStatus("SYSTEM");
            message.setContent(sender + " left room [" + roomId + "]");
            chatStatsService.recordMessage(message);
            return message;
        }

        RoomService.JoinResult joinResult = roomService.joinRoom(roomId, sender, secretKey);

        if (joinResult == RoomService.JoinResult.JOIN_LOCKED) {
            return buildSystemMessage(
                    roomId,
                    sender,
                    "ALERT: Room [" + roomId + "] is under attack. New joins are temporarily locked.",
                    "JOIN_LOCKED"
            );
        }

        if (joinResult == RoomService.JoinResult.DENIED) {
            return buildSystemMessage(
                    roomId,
                    sender,
                    "ACCESS DENIED: Wrong Secret Key for room [" + roomId + "]",
                    "DENIED"
            );
        }

        message.setSender(sender);
        message.setRoomId(roomId);
        message.setTimestamp(LocalTime.now().format(formatter));
        message.setIntegrityHash(null);
        message.setSecretKey(null);
        message.setIntegrityStatus("SYSTEM");

        if (message.getType() == ChatMessage.MessageType.JOIN) {
            message.setContent(sender + " joined room [" + roomId + "]");
            chatStatsService.recordMessage(message);
            return message;
        }

        return null;
    }

    @GetMapping("/history")
    @ResponseBody
    public List<ChatMessageEntity> getChatHistory(@RequestParam String roomId) {
        return chatMessageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    private ChatMessage buildSystemMessage(String roomId, String sender, String content, String status) {
        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setRoomId(roomId);
        message.setContent(content);
        message.setTimestamp(LocalTime.now().format(formatter));
        message.setType(ChatMessage.MessageType.CHAT);
        message.setIntegrityStatus(status);
        message.setIntegrityHash(null);
        message.setSecretKey(null);
        return message;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeValue(String value, String fallback) {
        String trimmed = safeTrim(value);
        return trimmed.isEmpty() ? fallback : trimmed;
    }

    private String buildIntegrityBase(String sender, String content, String roomId, String type, String secretKey) {
        return sender + "|" + content + "|" + roomId + "|" + type + "|" + secretKey;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}