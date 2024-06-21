package com.dj.model.scheme.commons;

import com.dj.model.scheme.table.TableBuilder;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author DJ
 * @Date 2024/6/8 3:06
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(TableBuilder.class)
public @interface EnableTableScheme {
    /**
     * modelPackages - 设置需要扫描的数据模型包,默认为当前项目
     * */
    String[] scanModelPackages() default {};
}
