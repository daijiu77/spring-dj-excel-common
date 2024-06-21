package com.dj.model.scheme.commons;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Author DJ
 * @Date 2024/6/9 20:34
 **/
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMapping {
    String fieldName() default "";
    String javaDataType() default "";
    int length() default 200;
    boolean isPrimaryKey() default false;
    boolean notNull() default false;
}
