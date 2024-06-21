package org.dj.excelcommon.excel.importdata;

import org.dj.excelcommon.excel.importdata.entities.HeadInfo;

import java.util.List;
import java.util.Map;

/**
 * @Author DJ
 * @Date 2024/6/14 10:21
 **/
public interface IExcelBuilder {
    void setSheetName(String name);
    <T> int createRows(List<T> entities, Class<T> entityType) throws Exception;
    <T> int createRow(T entity, Class<T> entityType) throws Exception;
    int createRow(FuncCellValue funcCellValue) throws Exception;
    int createRow(Map<String, Object> dataRowMap) throws Exception;
    List<HeadInfo> getHead() throws IllegalAccessException;
}
