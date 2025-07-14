package com.chenxiaofeng.aibi.base;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * SqlEntity sql实体
 * @author 尘小风
 */
@Data
@AllArgsConstructor
public class SqlEntity {

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 字段类型
     */
    private String columnType;
}