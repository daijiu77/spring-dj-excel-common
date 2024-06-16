package org.dj.excelcommon.excel.importdata;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dj.excelcommon.excel.EMethodType;
import org.dj.excelcommon.excel.importdata.entities.HeadInfo;
import org.dj.excelcommon.scanconfig.entities.ColumnInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author DJ
 * @Date 2024/6/14 12:28
 **/
public class ExcelBuilderImpl extends AbsExcelImportService implements IExcelBuilder {
    private Workbook workbook = null;
    private Sheet sheet = null;
    /**
     * headInfoFieldMap - key: HeadInfo 对象中属性名(小写), value: HeadInfo 对象中属性 Field 对象
     */
    private Map<String, Field> headInfoFieldMap = new HashMap<>();
    /**
     * entityInfo - 存储实体的类型， 属性名称与属性对象 Map, 其中属性名称为小写
     */
    private EntityInfo entityInfo = new EntityInfo();
    private List<HeadInfo> headInfos = new ArrayList<>();
    private int currentRowIndex = 0;
    private int currentColumnIndex = 0;

    public ExcelBuilderImpl(Sheet sheet, String tableOfConfig) throws Exception {
        this.sheet = sheet;
        workbook = sheet.getWorkbook();
        initExcelHead(sheet, tableOfConfig);
        Field[] fields = HeadInfo.class.getDeclaredFields();
        for (Field fd : fields) {
            headInfoFieldMap.put(fd.getName().toLowerCase(), fd);
        }
        currentRowIndex = tableInfo.getDataRowIndex();
    }

    @Override
    public void setSheetName(String name) {
        workbook.setSheetName(workbook.getSheetIndex(sheet), name);
    }

    @Override
    public <T> int createRows(List<T> entities, Class<T> entityType) throws Exception {
        int rows = 0;
        for (T t : entities) {
            rows += createRow(t, entityType);
        }
        return rows;
    }

    @Override
    public <T> int createRow(T entity, Class<T> entityType) throws Exception {
        if (entityInfo.getClsType() != entityType) {
            entityInfo.setClsType(entityType);
        }
        List<ColumnInfo> columnInfos = tableInfo.getColumnInfos();
        if (null == columnInfos) return 0;
        Row row = sheet.createRow(currentRowIndex);
        currentColumnIndex = 0;
        foreachColumnInfos(columnInfos, ci -> {
            currentColumnIndex++;
            String alias = ci.getAlias();
            if (alias.isEmpty()) {
                alias = ci.getName();
            }
            Object val = entityInfo.getFieldValue(entity, alias);
            if (!ci.getAllowEmpty()) {
                if (null == val) throw new Exception("[" + alias + "] can't be empty");
            }
            Cell cell = row.createCell(currentColumnIndex - 1);
            Class<?> fType = entityInfo.getFieldType(alias);
            setCellValue(cell, val, fType);
            setCellStyle(cell, ci.getStyle());
        });
        currentRowIndex++;
        return 1;
    }

    @Override
    public int createRow(FuncCellValue funcCellValue) throws Exception {
        List<ColumnInfo> columnInfos = tableInfo.getColumnInfos();
        if (null == columnInfos) return 0;
        Row row = sheet.createRow(currentRowIndex);
        currentColumnIndex = 0;
        foreachColumnInfos(columnInfos, ci -> {
            currentColumnIndex++;
            String name = ci.getName();
            String alias = ci.getAlias();
            String text = ci.getText();
            Object val = funcCellValue.cellValue(name, alias, text, currentRowIndex, ci.getIndex());
            if (!ci.getAllowEmpty()) {
                if (null == val) throw new Exception("[" + alias + "] can't be empty");
            }
            Class<?> dataType = null;
            if (null != val) dataType = val.getClass();
            Cell cell = row.createCell(currentColumnIndex - 1);
            setCellValue(cell, val, dataType);
            setCellStyle(cell, ci.getStyle());
        });
        currentRowIndex++;
        return 1;
    }

    @Override
    public int createRow(Map<String, Object> dataRowMap) throws Exception {
        if (null == dataRowMap) return 0;
        if (dataRowMap.isEmpty()) return 0;
        List<ColumnInfo> columnInfos = tableInfo.getColumnInfos();
        if (null == columnInfos) return 0;
        Map<String, Object> map = new HashMap<>();
        for (String key : dataRowMap.keySet()) {
            map.put(key.toLowerCase(), dataRowMap.get(key));
        }
        Row row = sheet.createRow(currentRowIndex);
        currentColumnIndex = 0;
        foreachColumnInfos(columnInfos, ci -> {
            currentColumnIndex++;
            String alias = ci.getAlias();
            if (alias.isEmpty()) {
                alias = ci.getName();
            }
            alias = alias.toLowerCase();
            if (!map.containsKey(alias)) {
                if (ci.getAlias().isEmpty()) return;
                alias = ci.getName().toLowerCase();
            }
            if (!map.containsKey(alias)) return;
            Object val = map.get(alias);
            if (!ci.getAllowEmpty()) {
                if (null == val) throw new Exception("[" + alias + "] can't be empty");
            }
            Class<?> dataType = null;
            if (null != val) dataType = val.getClass();
            Cell cell = row.createCell(ci.getIndex());
            setCellValue(cell, val, dataType);
            setCellStyle(cell, ci.getStyle());
        });
        currentRowIndex++;
        return 1;
    }

    @Override
    public List<HeadInfo> getHead() throws IllegalAccessException {
        if (null == tableInfo) return null;
        if (!headInfos.isEmpty()) return headInfos;
        List<ColumnInfo> columnInfos = tableInfo.getColumnInfos();
        for (ColumnInfo ci : columnInfos) {
            HeadInfo headInfo = new HeadInfo();
            setFieldValue(headInfo, "fieldName", ci.getName());
            setFieldValue(headInfo, "name", ci.getAlias());
            setFieldValue(headInfo, "text", ci.getText());
            setFieldValue(headInfo, "index", ci.getIndex());
            headInfos.add(headInfo);
        }
        return headInfos;
    }

    @Override
    protected Workbook getWorkbook() {
        return workbook;
    }

    private void setCellValue(Cell cell, Object val, Class<?> dataType) {
        if (null == val) {
            cell.setBlank();
            return;
        }
        if (String.class == dataType) {
            cell.setCellValue(val.toString());
        } else if ((int.class == dataType) || (float.class == dataType) || (double.class == dataType)) {
            double db = Double.parseDouble(val.toString());
            cell.setCellValue(db);
        } else if ((Date.class == dataType) || (Calendar.class == dataType)) {
            Date date = stringToDate(val.toString());
            cell.setCellValue(date);
        } else if (boolean.class == dataType) {
            boolean mbool = Boolean.parseBoolean(val.toString());
            cell.setCellValue(mbool);
        }
    }

    private void setFieldValue(Object obj, String fieldName, Object fieldValue) throws IllegalAccessException {
        if (null == fieldValue) return;
        String fnLower = fieldName.toLowerCase();
        if (!headInfoFieldMap.containsKey(fnLower))
            throw new IllegalAccessException("Property [" + fieldName + "] is not exist in the HeadInfo object.");
        Field field = headInfoFieldMap.get(fnLower);
        field.setAccessible(true);
        field.set(obj, fieldValue);
    }

    class EntityInfo {
        private Class<?> clsType;
        /**
         * fieldMap - key: 属性名称(小写), value: 属性 Field 对象
         */
        private Map<String, Field> fieldMap = new HashMap<>();

        public Class<?> getClsType() {
            return clsType;
        }

        public EntityInfo setClsType(Class<?> clsType) {
            this.clsType = clsType;
            fieldMap.clear();
            getFields(clsType);
            return this;
        }

        private void getFields(Class<?> clsType) {
            Class<?> parentType = clsType.getSuperclass();
            if (Object.class != parentType) {
                getFields(parentType);
            }
            Field[] fields = clsType.getDeclaredFields();
            for (Field fd : fields) {
                String fdLower = fd.getName().toLowerCase();
                if (fieldMap.containsKey(fdLower)) continue;
                fieldMap.put(fdLower, fd);
            }
        }

        public Map<String, Field> getFieldMap() {
            return fieldMap;
        }

        public Object getFieldValue(Object entity, String fieldName) {
            Object val = null;
            if (null == entity) return val;
            if (null == fieldName) fieldName = "";
            if (fieldName.isEmpty()) return val;
            Method getMethod = getMethodByName(entity.getClass(), fieldName, EMethodType.get);
            if (null != getMethod) {
                try {
                    val = getMethod.invoke(entity);
                    return val;
                } catch (Exception e) {
                    //
                }
            }
            String fnLower = fieldName.trim().toLowerCase();
            if (!fieldMap.containsKey(fnLower)) return val;
            Field field = fieldMap.get(fnLower);
            try {
                field.setAccessible(true);
                val = field.get(entity);
                val = convertTo(val, field.getType());
            } catch (Exception e) {
                //
            }
            return val;
        }

        public Class<?> getFieldType(String fieldName) {
            if (null == fieldName) fieldName = "";
            if (fieldName.isEmpty()) return null;
            String fnLower = fieldName.trim().toLowerCase();
            if (!fieldMap.containsKey(fnLower)) return null;
            Field field = fieldMap.get(fnLower);
            return field.getType();
        }
    }
}
