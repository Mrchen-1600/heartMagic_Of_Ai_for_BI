package com.chenxiaofeng.aibi.manager;

import com.chenxiaofeng.aibi.exception.BusinessException;
import com.github.rholder.retry.*;
import com.chenxiaofeng.aibi.common.ErrorCode;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * AIManager
 * @author 尘小风
 */
@Slf4j
@Service
public class AiManager {

    @Resource
    private ClientV4 clientV4;

    // 稳定的随机数
    private static final float STABLE_TEMPERATURE = 0.05f;

    // 不稳定的随机数
    private static final float UNSTABLE_TEMPERATURE = 0.99f;

    final String systemMessage = "\"你是一个资深的数据分析师和前端开发专家，接下来我会按照以下固定格式给你提供内容：\\n\" +\n" +
            "                \"分析需求：\\n\" +\n" +
            "                \"{数据分析的需求或者目标}\\n\" +\n" +
            "                \"原始数据：\\n\" +\n" +
            "                \"{csv格式的原始数据，用,作为分隔符}\\n\" +\n" +
            "                \"请根据这两部分内容，按照以下指定格式生成内容（不要输出任何多余的开头、结尾、注释）\\n\" +\n" +
            "                \"【【【【【\\n\" +\n" +
            "                \"{前端 Echarts V5 的 option 配置对象的js代码(输出json格式)，合理地将数据进行可视化，不要生成任何多余的内容，比如注释}\\n\" +\n" +
            "                \"【【【【【\\n\" +\n" +
            "                \"{明确的数据分析结论、越详细越好，不要生成任何多余的内容，比如注释}\n" +
            "                 【【【【【";


    /**
     * AI 对话（通用请求，参数未封装）
     * @param messages 对话消息
     * @param stream 是否流式返回
     * @param temperature 稳定程度
     * @return AI回答
     */
    public String doChat(List<ChatMessage> messages, Boolean stream, Float temperature) {
        // 构建请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }



    /**
     * 通用请求（简化消息传递）
     * @param userMessage
     * @param stream
     * @param temperature
     * @return
     */
    public String doChat(String userMessage, Boolean stream, Float temperature) {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        chatMessageList.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(userChatMessage);
        return doChat(chatMessageList, stream, temperature);
    }



    /**
     * 同步请求（通用请求基础上封装了请求类型为同步请求）
     *
     * @param userMessage
     * @param temperature
     * @return
     */
    public String doChat(String userMessage, Float temperature) {
        return doChat(userMessage, Boolean.FALSE, temperature);
    }



    /**
     * 同步请求且稳定
     *
     * @param userMessage
     * @return
     */
    public String doChat(String userMessage) {
        return doChat(userMessage, Boolean.FALSE, STABLE_TEMPERATURE);
    }


    /**
     * 同步请求且不稳定
     *
     * @param userMessage
     * @return
     */
    public String doUnstableChat(String userMessage) {
        return doChat(userMessage, Boolean.FALSE, UNSTABLE_TEMPERATURE);
    }



    /**
     * 同步请求具有重试
     * @param messages
     * @param stream
     * @param temperature
     * @return
     */
    private String retryDoChat(List<ChatMessage> messages, Boolean stream, Float temperature) {
        Retryer<String> retryer = RetryerBuilder.<String>newBuilder()
                .retryIfException() // 出现异常时进行重试
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 最大重试3次
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS)) // 每次重试间隔2秒
                .build();
        Callable<String> callable = () -> doChat(messages, stream, temperature);
        try {
            return retryer.call(callable);
        } catch (ExecutionException | RetryException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "多次重试后仍然无法成功调用AI平台，请检查网络或其他问题");
        }
    }


    /**
     * 同步请求带重试 传递消息参数封装
     * @param userMessage
     * @param stream 是否流式返回
     * @param temperature 稳定程度
     * @return AI回答
     */
    public String retryDoChat(String userMessage, Boolean stream, Float temperature) {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        chatMessageList.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(userChatMessage);
        return retryDoChat(chatMessageList, stream, temperature);
    }



    /**
     * 同步请求具有重试机会且稳定
     * @param userMessage
     * @return
     */
    public String retryDoChat(String userMessage) {
        return retryDoChat(userMessage, Boolean.FALSE, STABLE_TEMPERATURE);
    }



    /**
     * 同步请求具有重试机会且不稳定
     * @param userMessage
     * @return
     */
    public String retryDoUnstableChat(String userMessage) {
        return retryDoChat(userMessage, Boolean.FALSE, UNSTABLE_TEMPERATURE);
    }


    /**
     * 通用流式请求
     *
     * @param messages
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doStreamChat(List<ChatMessage> messages, Float temperature) {
        // 构建请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try {
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getFlowable();
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }



    /**
     * 通用流式请求（简化消息传递）
     *
     * @param userMessage
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doStreamChat(String userMessage, Float temperature) {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        chatMessageList.add(systemChatMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(userChatMessage);
        return doStreamChat(chatMessageList, temperature);
    }



    /**
     * 流式请求带重试机会
     * @param userMessage
     * @param temperature
     * @return
     */
    public Flowable<ModelData> RetrydoStreamChat(String userMessage, Float temperature) {
        Retryer<Flowable<ModelData>> retryer = RetryerBuilder.<Flowable<ModelData>>newBuilder()
                .retryIfException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                .build();
        Callable<Flowable<ModelData>> callable = () -> doStreamChat(userMessage, temperature);
        try {
            return retryer.call(callable);
        } catch (ExecutionException | RetryException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "多次重试后仍然无法成功调用AI平台，请检查网络或其他问题");
        }
    }
}