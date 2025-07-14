package com.chenxiaofeng.aibi.manager;

import com.chenxiaofeng.aibi.common.ErrorCode;
import com.chenxiaofeng.aibi.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * redis限流器
 * @author cxf
 */
@Component
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流操作
     *
     * @param key redisKey
     */
    public void doRateLimit(String key) {
        // 创建一个限流器
        RRateLimiter limiter = redissonClient.getRateLimiter(key);
        //过期时间
        limiter.expire(Duration.ofMinutes(1));
        // 初始化限流器，设置每秒最大许可数为 1
        limiter.trySetRate(RateType.OVERALL, 1, 1, RateIntervalUnit.SECONDS);
        //尝试获取许可，获取失败，则抛出异常
        if (!limiter.tryAcquire()) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}