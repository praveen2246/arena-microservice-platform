package com.socialmedia.user.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "notificationExchange";
    private static final String ROUTING_KEY = "notificationRoutingKey";

    public void sendFollowNotification(Long followerId, Long followingId, String followerUsername) {
        Map<String, Object> message = new HashMap<>();
        message.put("recipientId", followingId);
        message.put("content", followerUsername + " started following you!");
        message.put("type", "FOLLOW");
        message.put("senderId", followerId);

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, message);
        System.out.println("Sent follow notification to RabbitMQ: " + message);
    }
}
