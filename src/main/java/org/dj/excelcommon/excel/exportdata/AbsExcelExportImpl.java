package org.dj.excelcommon.excel.exportdata;

import org.apache.poi.ss.usermodel.Workbook;
import org.dj.excelcommon.excel.FuncReceiveEntity;
import org.dj.excelcommon.excel.FuncReceiveMap;
import org.springframework.lang.NonNull;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author DJ
 * @Date 2024/6/14 12:16
 **/
public abstract class AbsExcelExportImpl extends AbsExcelExportService implements IExcelExport{
    @Override
    public <T> void exportToEntityFromStream(InputStream inputStream, int sheetIndex, String tableOfConfig, Class<T> entityType, @NonNull FuncReceiveEntity funcReceiveEntity) throws Exception {
        Workbook workbook = getWorkbook(inputStream);
        toEntityFromStream(workbook, sheetIndex, tableOfConfig, entityType, funcReceiveEntity);
        workbook.close();
    }

    @Override
    public <T> void exportToEntityFromFile(String excelFile, int sheetIndex, String tableOfConfig, Class<T> entityType, @NonNull FuncReceiveEntity funcReceiveEntity) throws Exception {
        FileInputStream inputStream = getInputStream(excelFile);
        exportToEntityFromStream(inputStream, sheetIndex, tableOfConfig, entityType, funcReceiveEntity);
    }

    @Override
    public void exportToMapFromStream(InputStream inputStream, int sheetIndex, String tableOfConfig, @NonNull FuncReceiveMap funcReceiveMap) throws Exception {
        Workbook workbook = getWorkbook(inputStream);
        toMapFromStream(workbook, sheetIndex, tableOfConfig, funcReceiveMap);
        workbook.close();
    }

    @Override
    public void exportToMapFromFile(String excelFile, int sheetIndex, String tableOfConfig, @NonNull FuncReceiveMap funcReceiveMap) throws Exception {
        FileInputStream inputStream = getInputStream(excelFile);
        exportToMapFromStream(inputStream, sheetIndex, tableOfConfig, funcReceiveMap);
    }
    protected abstract Workbook getWorkbook(InputStream inputStream) throws Exception;
}
