package com.chenxiaofeng.aibi.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 测试 线程池流程

 * 线程池获取任务运行流程：
 * 1.来了一个任务，核心线程数没有用完，其中一个核心线程会直接处理这个任务
 * 2.当核心线程数都在处理任务时，新增加的任务会到队列中排队
 * 3.如果队列满了，最大线程数就会增加核心线程数来处理最新的任务（不是队列中的任务）
 *
 * @author 尘小风
 */
@Slf4j
@RestController
@Profile({"dev", "test", "local"})
@RequestMapping("/queue")
public class QueueController {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    //提交任务到线程池
    @GetMapping("/add")
    public void add(String name) {
        CompletableFuture.runAsync(() -> {
            log.info("任务执行中：" + name + "，执行线程：" + Thread.currentThread().getName());
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }, threadPoolExecutor);
    }


    @GetMapping("/get")
    public String get() {
        Map<String, Object> map = new HashMap<>();
        map.put("队列长度", threadPoolExecutor.getQueue().size());
        map.put("任务总数", threadPoolExecutor.getTaskCount());
        map.put("正在工作线程数", threadPoolExecutor.getActiveCount());
        map.put("已完成任务数", threadPoolExecutor.getCompletedTaskCount());
        return JSONUtil.toJsonStr(map);
    }
}