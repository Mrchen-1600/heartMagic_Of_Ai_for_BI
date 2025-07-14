package com.chenxiaofeng.aibi.model.dto.chart;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新生成图表信息请求
 *
 * @author 尘小风
 */
@Data
public class UpdateGenChartRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 生成的图表数据
     */
    private String genChart;

    @Serial
    private static final long serialVersionUID = 1L;
}