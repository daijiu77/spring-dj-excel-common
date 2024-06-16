package org.dj.excelcommon.excel.exportdata;

import org.dj.excelcommon.excel.FuncReceiveEntity;
import org.dj.excelcommon.excel.FuncReceiveMap;

import java.io.InputStream;

/**
 * @Author DJ
 * @Date 2024/6/14 10:46
 **/
public interface IExcelExport {
    <T> void exportToEntityFromStream(InputStream inputStream, int sheetIndex, String tableOfConfig, Class<T> entityType, FuncReceiveEntity funcReceiveEntity) throws Exception;
    <T> void exportToEntityFromFile(String excelFile, int sheetIndex, String tableOfConfig, Class<T> entityType, FuncReceiveEntity funcReceiveEntity) throws Exception;
    void exportToMapFromStream(InputStream inputStream, int sheetIndex, String tableOfConfig, FuncReceiveMap funcReceiveMap) throws Exception;
    void exportToMapFromFile(String excelFile, int sheetIndex, String tableOfConfig, FuncReceiveMap funcReceiveMap) throws Exception;
}
