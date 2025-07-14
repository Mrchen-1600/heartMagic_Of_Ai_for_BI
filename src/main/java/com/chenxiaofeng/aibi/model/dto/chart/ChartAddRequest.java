package com.chenxiaofeng.aibi.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 * @author 尘小风
 */
@Data
public class ChartAddRequest implements Serializable {

    /**
     * 图表名称
     */
    private String chartName;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 生成的分析结论
     */
    private String genResult;

    /**
     * 生成状态[0:等待1:运行中2:失败3:成功]
     */
    private Integer genStatus;

    /**
     * 执行信息
     */
    private String execMessage;

    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}