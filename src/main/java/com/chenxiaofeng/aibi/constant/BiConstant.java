package com.chenxiaofeng.aibi.constant;

import java.util.Arrays;
import java.util.List;

/**
 * ai常量
 *
 * @author 尘小风
 */
public interface BiConstant {


    /**
     * 截取Ai内容字符串
     */
    String
            AI_SPLIT_STR = "【【【【【";

    /**
     * 限流器key
     */
    String BI_REDIS_LIMITER_KEY = "getChartByAi-";

    /**
     * 文件后缀
     */
    List<String> VALID_FILE_SUFFIX_LIST = Arrays.asList("xlsx", "csv", "xls");
}