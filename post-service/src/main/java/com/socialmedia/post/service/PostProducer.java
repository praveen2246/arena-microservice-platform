package com.socialmedia.post.service;

import com.socialmedia.post.config.RabbitMQConfig;
import com.socialmedia.post.model.Post;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PostProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendPostCreatedEvent(Post post) {
        Map<String, Object> event = new HashMap<>();
        event.put("postId", post.getId());
        event.put("userId", post.getUserId());
        event.put("content", post.getContent());
        event.put("type", "POST_CREATED");

        rabbitTemplate.convertAndSend(RabbitMQConfig.POST_EXCHANGE, "post.created", event);
        System.out.println("Sent post created event to RabbitMQ: " + event);
    }
}
