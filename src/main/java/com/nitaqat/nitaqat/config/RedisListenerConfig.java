package com.nitaqat.nitaqat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.PatternTopic;

@Configuration
public class RedisListenerConfig {

    private final RedisKeyExpirationListener expirationListener;

    public RedisListenerConfig(RedisKeyExpirationListener expirationListener) {
        this.expirationListener = expirationListener;
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // ✅ Listen to expiration events
        container.addMessageListener(expirationListener, new PatternTopic("__keyevent@0__:expired"));

        return container;
    }
}
