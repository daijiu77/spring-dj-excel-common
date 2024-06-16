package org.dj.excelcommon.excel;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/13 12:43
 **/
@FunctionalInterface
public interface FuncReceiveEntity {
    boolean receiver(Object entity, int rowIndex);
}
