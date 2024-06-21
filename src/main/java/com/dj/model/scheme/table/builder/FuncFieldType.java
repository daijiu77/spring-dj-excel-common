package com.dj.model.scheme.table.builder;

/**
 * @Author DJ
 * @Date 2024/6/12 8:15
 **/
@FunctionalInterface
public interface FuncFieldType {
    String getFieldType(String javaType, int dataLength);
}
