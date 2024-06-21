package com.dj.model.scheme.table.scanner;

/**
 * @Author DJ
 * @Date 2024/6/11 2:54
 **/
@FunctionalInterface
public interface FuncEntityClass {
    boolean Result(Class<?> clsType, String clsName);
}
