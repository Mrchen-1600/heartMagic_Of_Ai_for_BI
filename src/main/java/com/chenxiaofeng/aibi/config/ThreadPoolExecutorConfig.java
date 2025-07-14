package com.chenxiaofeng.aibi.config;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置
 * @author 尘小风
 */
@Data
@ConfigurationProperties(prefix = "spring.task.execution.pool")
@Configuration
public class ThreadPoolExecutorConfig {

    private int coreSize;

    private int maxSize;

    private long keepAlive;

    private String threadName = "chart-ai-task-";

    /**
     * 线程池配置
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(threadName + thread.getId());
                return thread;
            }
        };

        return new ThreadPoolExecutor(
                coreSize, maxSize, keepAlive, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4),  // 工作队列（阻塞队列），最多有四个任务排队
                threadFactory //线程工厂
        );
    }
}