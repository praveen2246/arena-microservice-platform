package com.socialmedia.analytics.service;

import com.socialmedia.analytics.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AnalyticsListener {

    private final AtomicLong totalPosts = new AtomicLong(0);
    private final AtomicLong totalFollows = new AtomicLong(0);
    private final Map<Long, AtomicLong> userPostCounts = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitMQConfig.ANALYTICS_POST_QUEUE)
    public void handlePostEvent(Map<String, Object> message) {
        totalPosts.incrementAndGet();
        Long userId = Long.valueOf(message.get("userId").toString());
        userPostCounts.computeIfAbsent(userId, k -> new AtomicLong(0)).incrementAndGet();
        System.out.println("Analytics: New post tracked. Total posts: " + totalPosts.get());
    }

    @RabbitListener(queues = RabbitMQConfig.ANALYTICS_FOLLOW_QUEUE)
    public void handleFollowEvent(Map<String, Object> message) {
        if ("FOLLOW".equals(message.get("type"))) {
            totalFollows.incrementAndGet();
            System.out.println("Analytics: New follow tracked. Total follows: " + totalFollows.get());
        }
    }

    public Map<String, Object> getStats() {
        return Map.of(
            "totalPosts", totalPosts.get(),
            "totalFollows", totalFollows.get(),
            "userPostCounts", userPostCounts
        );
    }
}
