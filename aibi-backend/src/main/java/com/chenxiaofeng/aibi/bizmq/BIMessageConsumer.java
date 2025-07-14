package com.chenxiaofeng.aibi.bizmq;

import com.chenxiaofeng.aibi.manager.AiManager;
import com.rabbitmq.client.Channel;
import com.chenxiaofeng.aibi.common.ErrorCode;
import com.chenxiaofeng.aibi.constant.BiConstant;
import com.chenxiaofeng.aibi.constant.BiMqConstant;
import com.chenxiaofeng.aibi.exception.BusinessException;
import com.chenxiaofeng.aibi.model.entity.Chart;
import com.chenxiaofeng.aibi.model.enums.ChartStatusEnum;
import com.chenxiaofeng.aibi.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/*******************    💫 Codegeex Suggestion    *******************/
/**
 * bi 消息消费者
 * @author 尘小风
 * BIMessageConsumer 类用于处理 BI 消息队列中的消息，生成图表数据和分析建议。
 *
 * 该类通过 RabbitMQ 监听指定的消息队列，接收到消息后，进行以下处理：
 * 1. 解析消息内容，获取图表 ID。
 * 2. 根据 ID 查询图表信息，若图表不存在则拒绝消息。
 * 3. 更新图表状态为运行中。
 * 4. 向 AI 提问，获取图表数据和分析建议。
 * 5. 更新图表数据和分析建议，并将图表状态更新为成功。
 * 6. 确认消息处理成功，若处理失败则拒绝消息。
 *
 * 使用示例：
 * 无需手动实例化该类，通过 Spring 框架自动注入。
 *
 * 构造函数参数：
 * 无构造函数参数。
 *
 * 特殊使用限制或潜在的副作用：
 * - 消息内容必须为有效的图表 ID。
 * - 图表必须存在于数据库中。
 * - AI 返回的数据格式必须符合预期。
 * - 若消息处理失败，消息将被拒绝并重新入队。
 *
 * 作者：尘小风
 */
@Slf4j
@Component
public class BIMessageConsumer {

    @Resource
    private ChartService chartService;

    @Resource
    private AiManager aiManager;



    //指定程序监听的消息队列和确认机制
    @RabbitListener(queues = { BiMqConstant.BI_CHART_QUEUE_NAME }, ackMode = "MANUAL")
    public void biReceiveMessage(String message, Channel channel, @Header(value = AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("biReceiveMessage message = {} deliveryTag = {}", message, deliveryTag);
        try {
            if (StringUtils.isBlank(message)) {
                //如果出现异常，要拒绝掉消息，让消息进入死信队列
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息为空");
            }

            long chartId = Long.parseLong(message);
            Chart chart = chartService.getById(chartId);

            if (ObjectUtils.isEmpty(chart)) {
                //如果出现异常，要拒绝掉消息，让消息进入死信队列
                channel.basicNack(deliveryTag, false, false);
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图表不存在");
            }

            //修改图表状态为 running
            boolean updateRes = chartService.handleUpdateChartStatus(chartId, ChartStatusEnum.RUNNING.getValue());
            if (!updateRes) {
                //如果出现异常，要拒绝掉消息，让消息进入死信队列
                channel.basicNack(deliveryTag, false, false);
                chartService.handleChartUpdateError(chartId, "更新图表状态执行中失败");
                return;
            }

            //向AI提问
            String userInput = chartService.handleUserInput(chart);
            String aiRes = aiManager.retryDoChat(userInput);
            //处理AI返回数据，得到 图表数据 和 分析建议
            String[] aiData = aiRes.split(BiConstant.AI_SPLIT_STR);
            log.info("aiData len = {} data = {}", aiData.length, aiRes);
            if (aiData.length < 3) {
                channel.basicNack(deliveryTag, false, false);
                chartService.handleChartUpdateError(chartId, "Ai生成有误");
                return;
            }
            String genChart = aiData[ 1 ].trim();
            String genResult = aiData[ 2 ].trim();

            //更新 图表数据
            Chart updateChart = new Chart();
            updateChart.setId(chartId);
            updateChart.setGenChart(genChart);
            updateChart.setGenResult(genResult);
            updateChart.setGenStatus(ChartStatusEnum.SUCCEED.getValue());

            if (!chartService.updateById(updateChart)) {
                channel.basicNack(deliveryTag, false, false);
                chartService.handleChartUpdateError(chartId, "更新图表失败");
            }

            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ex) {
                log.error("拒绝消息失败 error = {}", ex.getMessage());
            }
            log.error("任务处理失败 message = {} deliveryTag = {} error = {}", message, deliveryTag, e.getMessage());
        }
    }
}
