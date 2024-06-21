package com.dj.model.scheme.commons;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 16:25
 **/
@FunctionalInterface
public interface FuncForeachSrcProperty {
    /**
     * @param srcFieldType Type of source-field
     * @param srcPropertyName Name of source-field
     * @param srcPropertyValue Value of source-field
     * @return if return result is true, then use value of source-field to set value of target-field
     * **/
    boolean propertyItem(Class<?> srcFieldType, String srcPropertyName, Object srcPropertyValue);
}
