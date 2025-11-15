package com.nitaqat.nitaqat.config;


import com.nitaqat.nitaqat.service.ActivityService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener implements MessageListener {

    private final ActivityService activityService;

    public RedisKeyExpirationListener(ActivityService activityService) {
        this.activityService = activityService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        System.out.println("🔥 EXPIRED KEY: " + expiredKey);
        if (expiredKey.startsWith("USER_SESSION_")) {
            activityService.onLogout(expiredKey, "system", "session-timeout");
        }
    }



}
