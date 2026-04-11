package com.entangle.messaging.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RoomService {

    private final ConcurrentHashMap<String, String> roomSecrets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> suspiciousAttempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> joinLockedRooms = new ConcurrentHashMap<>();

    private static final int MAX_SUSPICIOUS_ATTEMPTS = 3;

    public synchronized JoinResult joinRoom(String roomId, String secretKey) {
        if (!roomSecrets.containsKey(roomId)) {
            roomSecrets.put(roomId, secretKey);
            suspiciousAttempts.put(roomId, 0);
            joinLockedRooms.put(roomId, false);
            return JoinResult.ALLOWED;
        }

        boolean correctKey = roomSecrets.get(roomId).equals(secretKey);

        if (correctKey) {
            return JoinResult.ALLOWED;
        }

        registerSuspiciousAttempt(roomId);

        if (isJoinLocked(roomId)) {
            return JoinResult.JOIN_LOCKED;
        }

        return JoinResult.DENIED;
    }

    public synchronized AccessResult validateMessageAccess(String roomId, String secretKey) {
        if (!roomSecrets.containsKey(roomId)) {
            return AccessResult.DENIED;
        }

        if (isJoinLocked(roomId)) {
            return AccessResult.DENIED;
        }

        boolean correctKey = roomSecrets.get(roomId).equals(secretKey);

        if (correctKey) {
            return AccessResult.ALLOWED;
        }

        return AccessResult.DENIED;
    }

    public synchronized void registerTamperEvent(String roomId) {
        registerSuspiciousAttempt(roomId);
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