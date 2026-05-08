package com.socialmedia.notification.service;

import com.socialmedia.notification.model.Notification;
import com.socialmedia.notification.repository.NotificationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationListener {

    @Autowired
    private NotificationRepository notificationRepository;

    @RabbitListener(queues = "${rabbitmq.queue.notification}")
    public void receiveNotification(Map<String, Object> message) {
        System.out.println("Received notification message: " + message);
        
        Notification notification = Notification.builder()
                .recipientId(Long.valueOf(message.get("recipientId").toString()))
                .content(message.get("content").toString())
                .type(message.get("type").toString())
                .isRead(false)
                .build();
        
        notificationRepository.save(notification);
    }

    @RabbitListener(queues = "postQueue")
    public void receivePostEvent(Map<String, Object> message) {
        System.out.println("Received post event: " + message);
        
        // In a real app, we would fetch followers here and notify each.
        // For now, we'll just log it or create a placeholder notification.
        Notification notification = Notification.builder()
                .recipientId(0L) // Placeholder for "global" or system notification
                .content("User " + message.get("userId") + " created a new post: " + message.get("content"))
                .type("POST")
                .isRead(false)
                .build();
        
        notificationRepository.save(notification);
    }
}
