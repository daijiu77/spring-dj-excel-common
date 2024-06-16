package org.dj.excelcommon.excel.exportdata;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.dj.excelcommon.excel.AbsExcelService;
import org.dj.excelcommon.excel.EMethodType;
import org.dj.excelcommon.excel.FuncReceiveEntity;
import org.dj.excelcommon.excel.FuncReceiveMap;
import org.dj.excelcommon.scanconfig.entities.ColumnInfo;
import org.dj.excelcommon.scanconfig.entities.TableInfo;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/13 13:12
 **/
public abstract class AbsExcelExportService extends AbsExcelService implements IExcelExport {
    private String tableOfConfig;
    private Map<Integer, Method> setMethodMap = new HashMap<>();
    private Map<Integer, ColumnInfo> columnInfoMap = new HashMap<>();

    protected <T> T getEntityFromRow(Row row, int rowIndex, Class<T> entityType, String tableOfConfig) throws Exception {
        TableInfo tableInfo = getTableInfo(tableOfConfig);
        int dataRowIndex = tableInfo.getDataRowIndex();
        if (rowIndex == (dataRowIndex - 1)) {
            if (!tableOfConfig.equals(this.tableOfConfig)) {
                this.tableOfConfig = tableOfConfig;
                initSetMethodMap(row, entityType, tableInfo);
            }
        }
        if (dataRowIndex > rowIndex) return null;
        if (setMethodMap.isEmpty())
            throw new Exception("In the attribute table=" + tableOfConfig + " of config file don't find any matching column.");
        T entity = entityType.getDeclaredConstructor().newInstance();
        int columnCount = row.getLastCellNum();
        for (int i = 0; i < columnCount; i++) {
            if (!setMethodMap.containsKey(i)) continue;
            Cell cell = row.getCell(i);
            Object val = getValue(cell);
            if (null == val) continue;
            Method method = setMethodMap.get(i);
            Class<?> paraType = method.getParameterTypes()[0];
            val = convertTo(val, paraType);
            try {
                method.invoke(entity, val);
            } catch (Exception e) {
                //
            }
        }
        return entity;
    }

    protected Map<String, Object> getMapFromRow(Row row, int rowIndex, String tableOfConfig) throws Exception {
        TableInfo tableInfo = getTableInfo(tableOfConfig);
        int dataRowIndex = tableInfo.getDataRowIndex();
        if (rowIndex == (dataRowIndex - 1)) {
            if (!tableOfConfig.equals(this.tableOfConfig)) {
                this.tableOfConfig = tableOfConfig;
                initColumnInfoMap(row, tableInfo.getColumnInfos());
            }
        }
        if (dataRowIndex > rowIndex) return null;
        int columnCount = row.getLastCellNum();
        Map<String, Object> resultMap = new HashMap<>();
        for (int i = 0; i < columnCount; i++) {
            if (!columnInfoMap.containsKey(i)) continue;
            Cell cell = row.getCell(i);
            Object val = getValue(cell);
            ColumnInfo columnInfo = columnInfoMap.get(i);
            String key = columnInfo.getAlias();
            if (null == key) key = "";
            if (key.isEmpty()) {
                key = columnInfo.getName();
            }
            if (resultMap.containsKey(key)) continue;
            resultMap.put(key, val);
        }
        return resultMap;
    }

    protected <T> void toEntityFromStream(Workbook workbook, int sheetIndex, String tableOfConfig, Class<T> entityType, FuncReceiveEntity funcReceiveEntity) throws Exception{
        execute(workbook, sheetIndex, ((row, rowIndex) -> {
            T entity = null;
            try {
                entity = getEntityFromRow(row, rowIndex, entityType, tableOfConfig);
            } catch (Exception e) {
                //
            }
            if (null == entity) return true;
            return funcReceiveEntity.receiver(entity, rowIndex);
        }));
    }

    protected void toMapFromStream(Workbook workbook, int sheetIndex, String tableOfConfig, FuncReceiveMap funcReceiveMap) throws Exception{
        execute(workbook, sheetIndex, ((row, rowIndex) -> {
            Map<String, Object> dataMap = null;
            try {
                dataMap = getMapFromRow(row, rowIndex, tableOfConfig);
            } catch (Exception e) {
                //
            }
            if (null == dataMap) return true;
            return funcReceiveMap.receiver(dataMap, rowIndex);
        }));
    }

    protected FileInputStream getInputStream(String excelFile) throws Exception {
        if (null == excelFile) throw new Exception("Excel file can't be null!");
        File file = new File(excelFile);
        if (!file.exists()) throw new Exception("[" + excelFile + "] is not exist!");
        return new FileInputStream(excelFile);
    }

    private void execute(Workbook workbook, int sheetIndex, FuncExcelRow funcExcelRow){
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        int rowCount = sheet.getLastRowNum() + 1;
        for (int i = 0; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (!funcExcelRow.dataRow(row, i)) break;
        }
    }

    private void initColumnInfoMap(Row row, List<ColumnInfo> columnInfos) {
        columnInfoMap.clear();
        int columnCount = row.getLastCellNum();
        for (int i = 0; i < columnCount; i++) {
            String txt = getTextFromChild(row, i);
            if (txt.isEmpty()) continue;
            ColumnInfo columnInfo = getColumnInfoByText(columnInfos, txt);
            if (null == columnInfo) continue;
            columnInfoMap.put(i, columnInfo);
        }
    }

    private void initSetMethodMap(Row row, Class<?> clsType, TableInfo tableInfo) {
        setMethodMap.clear();
        List<ColumnInfo> columnInfos = tableInfo.getColumnInfos();
        if (null == columnInfos) return;
        if (columnInfos.isEmpty()) return;
        int columnCount = row.getLastCellNum();
        for (int i = 0; i < columnCount; i++) {
            String txt = getTextFromChild(row, i);
            if (txt.isEmpty()) continue;
            Method method = getMethodByText(columnInfos, clsType, txt, EMethodType.set);
            if (null == method) continue;
            setMethodMap.put(i, method);
        }
    }

    private String getTextFromChild(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        String txt = cell.getStringCellValue().trim();
        if (!txt.isEmpty()) return txt;
        int num = row.getRowNum();
        Sheet sheet = row.getSheet();
        Row row1 = sheet.getRow(num - 1);
        cell = row1.getCell(columnIndex);
        txt = cell.getStringCellValue().trim();
        return txt;
    }

    private Object getValue(Cell cell) {
        Object val = null;
        //匹配类型数据
        if (cell != null) {
            CellType cellType = cell.getCellType();
            switch (cellType) {
                case STRING: //字符串
                    //System.out.print("[String类型]");
                    val = cell.getStringCellValue();
                    break;
                case BOOLEAN: //布尔类型
                    //System.out.print("[boolean类型]");
                    val = cell.getBooleanCellValue();
                    break;
                case BLANK: //空
                    //System.out.print("[BLANK类型]");
                    break;
                case NUMERIC: //数字（日期、普通数字）
                    //System.out.print("[NUMERIC类型]");
                    if (HSSFDateUtil.isCellDateFormatted(cell)) { //日期
                        //System.out.print("[日期]");
                        val = cell.getDateCellValue();
                        //cellValue = new DateTime(date).toString("yyyy-MM-dd");
                    } else {
                        //不是日期格式，防止数字过长
                        //System.out.print("[转换为字符串输出]");
                        val = cell.getNumericCellValue();
                    }
                    break;
                case ERROR:
                    System.out.print("[数据类型错误]");
                    break;
                default:
                    val = cell.getStringCellValue();
            }
        }
        return val;
    }

    @FunctionalInterface
    protected interface FuncExcelRow {
        boolean dataRow(Row row, int rowIndex);
    }
}
