package com.socialmedia.analytics.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ANALYTICS_POST_QUEUE = "analyticsPostQueue";
    public static final String ANALYTICS_FOLLOW_QUEUE = "analyticsFollowQueue";
    
    public static final String POST_EXCHANGE = "postExchange";
    public static final String FOLLOW_EXCHANGE = "notificationExchange";

    @Bean
    public Queue analyticsPostQueue() {
        return new Queue(ANALYTICS_POST_QUEUE);
    }

    @Bean
    public Queue analyticsFollowQueue() {
        return new Queue(ANALYTICS_FOLLOW_QUEUE);
    }

    @Bean
    public TopicExchange postExchange() {
        return new TopicExchange(POST_EXCHANGE);
    }

    @Bean
    public TopicExchange followExchange() {
        return new TopicExchange(FOLLOW_EXCHANGE);
    }

    @Bean
    public Binding analyticsPostBinding(Queue analyticsPostQueue, TopicExchange postExchange) {
        return BindingBuilder.bind(analyticsPostQueue).to(postExchange).with("post.created");
    }

    @Bean
    public Binding analyticsFollowBinding(Queue analyticsFollowQueue, TopicExchange followExchange) {
        return BindingBuilder.bind(analyticsFollowQueue).to(followExchange).with("notificationRoutingKey");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
