package edu.rutmiit.demo.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String EXCHANGE_NAME = "membership-exchange";
    public static final String ROUTING_KEY_MEMBERSHIP_CREATED = "membership.created";

    @Bean
    public TopicExchange membershipsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
}
