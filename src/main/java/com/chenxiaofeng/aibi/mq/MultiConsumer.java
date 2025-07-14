package com.chenxiaofeng.aibi.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;

/**
 * 多消费者
 */
public class MultiConsumer {

    private static final String TASK_QUEUE_NAME = "multi_queue";

    public static void main(String[] argv) throws Exception {

        //1.创建链接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");


        //2.建立链接、创建频道
        final Connection connection = factory.newConnection();
        for (int i = 0; i < 2; i++) {
            final Channel channel = connection.createChannel();


            //3.创建（声明）消息队列
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            // 在控制台打印等待接收消息的提示
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            //控制每个消费者的处理任务积压数，这里即为设置每个消费者最多同时处理1个任务，这样RabbitMQ就会在给消费者新消息之前等待先前的消息被确认
            channel.basicQos(1);


            //4.定义如何处理消息,创建一个新的DeliverCallback来处理接收到的消息
            int finalI = i;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                // 将消息体转换为字符串
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

                try {
                    System.out.println(" [x] Received '" + "编号:"+ finalI + ":" + message + "'");
                    //处理工作,模拟处理消息所花费的时间,机器处理能力有限(接收一条消息,5秒后再接收下一条消息)
                    Thread.sleep(5000);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);

                } finally {
                    System.out.println(" [x] Done");
                    // 手动发送应答,告诉RabbitMQ消息已经被处理
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }
            };

            //5.开始消费监听队列中的消息   第二个参数autoack默认为false —— 消息确认机制
            /*
            消息队列如何确保消费者已经成功取出消息呢？它依赖一个称为消息确认的机制。当消费者从队列中取走消息后，必须对此进行确认。
            这就像在收到快递后确认收货一样，这样消息队列才能知道消费者已经成功取走了消息，并能安心地停止传输。
            自动ack的方式只要队列有消息，RabbitMQ会源源不断的把消息推送给客户端，而不管客户端能否消费的完。因此建议将 autoack 设置为 false，根据实际情况手动进行确认。
            */
            channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> {
            });
        }
    }
}