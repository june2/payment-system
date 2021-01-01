package com.kakao.pay.config;

import com.kakao.pay.util.LockerUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

@Configuration
public class LockConfig {
    @Bean
    public LockerUtil locker(RedisConnectionFactory connectionFactory ) {
        return new LockerUtil( connectionFactory, "cardinfo" );
    }
}