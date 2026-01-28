package com.nitaqat.nitaqat.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    private final StringRedisTemplate redisTemplate;
    private final RedisConnectionFactory connectionFactory;

    public RedisConfig(StringRedisTemplate redisTemplate, RedisConnectionFactory connectionFactory) {
        this.redisTemplate = redisTemplate;
        this.connectionFactory = connectionFactory;
    }

    @PostConstruct
    public void enableKeyspaceNotifications() {
        try (var connection = connectionFactory.getConnection()) {
            connection.setConfig("notify-keyspace-events", "Ex");
            System.out.println("✅ Redis keyspace notifications enabled: Ex");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to enable Redis keyspace notifications: " + e.getMessage());
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // ✅ Use JSON serializer that understands LocalDateTime
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(new ObjectMapper().registerModule(new JavaTimeModule()));

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        return template;
    }
}
