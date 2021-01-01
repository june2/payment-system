package com.kakao.pay.config;

import com.kakao.pay.util.LockerUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;
import org.springframework.integration.support.locks.LockRegistry;

import javax.sql.DataSource;

@Configuration
public class LockConfig {
    @Bean
    public LockRepository lockRepository(DataSource dataSource) {
        return new DefaultLockRepository(dataSource);
    }

    @Bean
    public LockRegistry lockRegistry(LockRepository repository) {
        return new JdbcLockRegistry(repository);
    }

    @Bean
    public LockerUtil locker(RedisConnectionFactory connectionFactory ) {
        return new LockerUtil( connectionFactory, "cardinfo" );
    }
}