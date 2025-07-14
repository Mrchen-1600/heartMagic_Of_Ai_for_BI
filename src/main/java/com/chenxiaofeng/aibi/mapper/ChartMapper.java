package com.chenxiaofeng.aibi.mapper;

import com.chenxiaofeng.aibi.base.SqlEntity;
import com.chenxiaofeng.aibi.model.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Entity com.chenxiaofeng.aibi.model.entity.Chart
 */
public interface ChartMapper extends BaseMapper<Chart> {

    /**
     * 创建 图表信息原始数据表
     *
     * @param chartId 图表id
     * @param colList 字段
     * @return boolean
     */
    boolean creatChartTable(@Param("chartId") Long chartId, @Param("colList") List<SqlEntity> colList);

    /**
     * 插入图表信息原始数据
     *
     * @param chartId 图表id
     * @param dataMap 插入数据
     * @return 数据id
     */
    int insertBatchChart(@Param("chartId") Long chartId, @Param("columns") List<String> columns, @Param("data") List<Map<String, Object>> dataMap);
}




