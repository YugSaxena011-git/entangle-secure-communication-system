package com.entangle.messaging.stats;

public class ChatStats {

    private int totalMessages;
    private int priorityMessages;
    private int tamperedMessages;
    private int verifiedMessages;
    private int joinEvents;
    private int leaveEvents;

    public ChatStats() {
    }

    public int getTotalMessages() {
        return totalMessages;
    }

    public void setTotalMessages(int totalMessages) {
        this.totalMessages = totalMessages;
    }

    public int getPriorityMessages() {
        return priorityMessages;
    }

    public void setPriorityMessages(int priorityMessages) {
        this.priorityMessages = priorityMessages;
    }

    public int getTamperedMessages() {
        return tamperedMessages;
    }

    public void setTamperedMessages(int tamperedMessages) {
        this.tamperedMessages = tamperedMessages;
    }

    public int getVerifiedMessages() {
        return verifiedMessages;
    }

    public void setVerifiedMessages(int verifiedMessages) {
        this.verifiedMessages = verifiedMessages;
    }

    public int getJoinEvents() {
        return joinEvents;
    }

    public void setJoinEvents(int joinEvents) {
        this.joinEvents = joinEvents;
    }

    public int getLeaveEvents() {
        return leaveEvents;
    }

    public void setLeaveEvents(int leaveEvents) {
        this.leaveEvents = leaveEvents;
    }
}