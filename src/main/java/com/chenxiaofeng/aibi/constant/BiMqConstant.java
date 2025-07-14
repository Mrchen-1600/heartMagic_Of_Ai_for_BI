package com.chenxiaofeng.aibi.constant;

/**
 * Bi mq 常量
 * @author 尘小风
 */
public interface BiMqConstant {

    /**
     * bi 交换机
     */
    String BI_CHART_EXCHANGE_NAME = "bi-chart-exchange";

    /**
     * bi 路由键
     */
    String BI_CHART_ROUTING_KEY = "bi-chart-routing";

    /**
     * bi 队列
     */
    String BI_CHART_QUEUE_NAME = "bi-chart-queue";

    /**
     * bi 消息过期时间
     */
    String BI_CHART_MESSAGE_EXPIRED = "20000";

    /**
     * bi 重试交换机
     */
    String BI_CHART_RELOAD_EXCHANGE_NAME = "bi-chart-reload-exchange";

    /**
     * bi 重试路由键
     */
    String BI_CHART_RELOAD_ROUTING_KEY = "bi-chart-reload-routing";

    /**
     * bi 重试队列
     */
    String BI_CHART_RELOAD_QUEUE_NAME = "bi-chart-reload-queue";

    /**
     * bi 死信交换机
     */
    String BI_CHART_DLX_EXCHANGE_NAME = "bi-chart-dlx-exchange";

    /**
     * bi 死信队列
     */
    String BI_CHART_DLX_QUEUE_NAME = "bi-chart-dlx-queue";

    /**
     * bi 死信路由键
     */
    String BI_CHART_DLX_ROUTING_KEY = "bi-chart-dlx-routing";
}