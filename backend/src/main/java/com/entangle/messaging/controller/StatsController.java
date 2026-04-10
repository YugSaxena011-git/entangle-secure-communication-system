package com.entangle.messaging.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.entangle.messaging.stats.ChatStats;
import com.entangle.messaging.stats.ChatStatsService;

@RestController
public class StatsController {

    private final ChatStatsService chatStatsService;

    public StatsController(ChatStatsService chatStatsService) {
        this.chatStatsService = chatStatsService;
    }

    @GetMapping("/stats")
    public ChatStats getStats() {
        return chatStatsService.getStats();
    }
}