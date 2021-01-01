package com.kakao.pay.util;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.locks.Lock;


public class LockerUtil {

    private LockRegistry lockRegistry;
    private String registryKey;
    private Lock lock;

    public LockerUtil(RedisConnectionFactory connectionFactory, String registryKey) {
        this.registryKey = registryKey;
        this.lockRegistry = new RedisLockRegistry(connectionFactory, registryKey, 10_000);
    }

    public Lock lock(String lockKey) {
        this.lock = lockRegistry.obtain(lockKey);
        return this.lock;
    }

    public void unlock() {
        this.lock.unlock();
    }
}
