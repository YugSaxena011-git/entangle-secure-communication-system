package com.entangle.messaging.stats;

import org.springframework.stereotype.Service;

import com.entangle.messaging.model.ChatMessage;

@Service
public class ChatStatsService {

    private final ChatStats stats = new ChatStats();

    public synchronized void recordMessage(ChatMessage message) {
        if (message == null || message.getType() == null) {
            return;
        }

        switch (message.getType()) {
            case CHAT:
                stats.setTotalMessages(stats.getTotalMessages() + 1);
                break;

            case PRIORITY:
                stats.setTotalMessages(stats.getTotalMessages() + 1);
                stats.setPriorityMessages(stats.getPriorityMessages() + 1);
                break;

            case JOIN:
                stats.setJoinEvents(stats.getJoinEvents() + 1);
                break;

            case LEAVE:
                stats.setLeaveEvents(stats.getLeaveEvents() + 1);
                break;
        }

        if ("VERIFIED".equals(message.getIntegrityStatus())) {
            stats.setVerifiedMessages(stats.getVerifiedMessages() + 1);
        }

        if ("TAMPERED".equals(message.getIntegrityStatus())) {
            stats.setTamperedMessages(stats.getTamperedMessages() + 1);
        }
    }

    public synchronized ChatStats getStats() {
        return stats;
    }
}