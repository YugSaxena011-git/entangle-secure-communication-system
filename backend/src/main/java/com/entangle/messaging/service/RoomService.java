package com.entangle.messaging.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final ConcurrentHashMap<String, String> roomSecrets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> suspiciousAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> joinLockedRooms = new ConcurrentHashMap<>();

    private static final int MAX_SUSPICIOUS_ATTEMPTS = 5;

    public synchronized JoinResult joinRoom(String roomId, String secretKey) {
        if (roomId == null || roomId.trim().isEmpty()) return JoinResult.DENIED;
        if (secretKey == null || secretKey.trim().isEmpty()) return JoinResult.DENIED;

        String cleanRoomId = roomId.trim();
        String cleanSecretKey = secretKey.trim();

        if (isJoinLocked(cleanRoomId)) return JoinResult.JOIN_LOCKED;

        if (!roomSecrets.containsKey(cleanRoomId)) {
            roomSecrets.put(cleanRoomId, cleanSecretKey);
            suspiciousAttempts.put(cleanRoomId, 0);
            joinLockedRooms.put(cleanRoomId, false);
            return JoinResult.ALLOWED;
        }

        if (roomSecrets.get(cleanRoomId).equals(cleanSecretKey)) {
            suspiciousAttempts.put(cleanRoomId, 0);
            return JoinResult.ALLOWED;
        }

        registerSuspiciousAttempt(cleanRoomId);
        return isJoinLocked(cleanRoomId) ? JoinResult.JOIN_LOCKED : JoinResult.DENIED;
    }

    public synchronized AccessResult validateMessageAccess(String roomId, String secretKey) {
        if (roomId == null || roomId.trim().isEmpty()) return AccessResult.DENIED;
        if (secretKey == null || secretKey.trim().isEmpty()) return AccessResult.DENIED;

        String cleanRoomId = roomId.trim();
        String cleanSecretKey = secretKey.trim();

        if (isJoinLocked(cleanRoomId)) return AccessResult.DENIED;

        if (!roomSecrets.containsKey(cleanRoomId)) {
            roomSecrets.put(cleanRoomId, cleanSecretKey);
            suspiciousAttempts.put(cleanRoomId, 0);
            joinLockedRooms.put(cleanRoomId, false);
            return AccessResult.ALLOWED;
        }

        if (roomSecrets.get(cleanRoomId).equals(cleanSecretKey)) {
            return AccessResult.ALLOWED;
        }

        registerSuspiciousAttempt(cleanRoomId);
        return AccessResult.DENIED;
    }

    public synchronized void registerTamperEvent(String roomId) {
        if (roomId != null) registerSuspiciousAttempt(roomId.trim());
    }

    public synchronized boolean isJoinLocked(String roomId) {
        return joinLockedRooms.getOrDefault(roomId, false);
    }

    public synchronized int getSuspiciousAttempts(String roomId) {
        return suspiciousAttempts.getOrDefault(roomId, 0);
    }

    private void registerSuspiciousAttempt(String roomId) {
        int count = suspiciousAttempts.getOrDefault(roomId, 0) + 1;
        suspiciousAttempts.put(roomId, count);

        if (count >= MAX_SUSPICIOUS_ATTEMPTS) {
            joinLockedRooms.put(roomId, true);
        }
    }

    public enum JoinResult {
        ALLOWED,
        DENIED,
        JOIN_LOCKED
    }

    public enum AccessResult {
        ALLOWED,
        DENIED
    }
}