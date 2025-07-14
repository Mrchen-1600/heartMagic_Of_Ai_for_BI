package com.chenxiaofeng.aibi.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * 图表信息创建请求
 *
 * @author 尘小风
 */
@Data
public class ChartCreatRequest implements Serializable {

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表名称
     */
    private String chartName;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;

    private static final long serialVersionUID = 1L;
}