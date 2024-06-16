package org.dj.excelcommon.excel.importdata;

/**
 * @Author DJ
 * @Date 2024/6/14 19:59
 **/
@FunctionalInterface
public interface FuncCellValue {
    Object cellValue(String columnName, String alias, String columnText, int rowIndex, int columnIndex);
}
