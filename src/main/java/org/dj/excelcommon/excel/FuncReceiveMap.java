package org.dj.excelcommon.excel;

import java.util.Map;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/13 17:07
 **/
@FunctionalInterface
public interface FuncReceiveMap {
    boolean receiver(Map<String, Object> dataMap, int rowIndex);
}
