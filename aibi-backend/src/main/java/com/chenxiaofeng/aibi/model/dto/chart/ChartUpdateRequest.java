package com.chenxiaofeng.aibi.model.dto.chart;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新请求
 * @author 尘小风
 */
@Data
public class ChartUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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
     * 生成的图表数据
     */
    private String genChart;

    /**
     * 生成的分析结论
     */
    private String genResult;


    @Serial
    private static final long serialVersionUID = 1L;
}