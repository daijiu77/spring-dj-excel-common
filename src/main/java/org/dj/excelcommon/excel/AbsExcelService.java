package org.dj.excelcommon.excel;

import org.dj.excelcommon.scanconfig.AbsAnalysisConfig;
import org.dj.excelcommon.scanconfig.entities.ColumnInfo;
import org.dj.excelcommon.scanconfig.entities.TableInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/13 17:53
 **/
public abstract class AbsExcelService {
    private Class<?> clsType = null;

    /**
     * getMethodMap - key: 对象的字段名称(小写), value: 对象的 get 方法
     */
    private final Map<String, Method> getMethodMap = new HashMap<>();
    /**
     * setMethodMap - key: 对象的字段名称(小写), value: 对象的 set 方法
     */
    private final Map<String, Method> setMethodMap = new HashMap<>();

    protected Method getMethodByText(List<ColumnInfo> columnInfos, Class<?> clsType, String text, EMethodType methodType) {
        Method method = null;
        ColumnInfo columnInfo = getColumnInfoByText(columnInfos, text);
        if (null == columnInfo) return method;
        String alias = columnInfo.getAlias();
        if (null == alias) return method;
        if (alias.isEmpty()) return method;
        return getMethodByName(clsType, alias, methodType);
    }

    protected Method getMethodByName(Class<?> clsType, String fieldName, EMethodType methodType) {
        if (null == clsType) return null;
        if (null == fieldName) return null;
        if (fieldName.isEmpty()) return null;
        if (this.clsType != clsType) {
            this.clsType = clsType;
            getMethodMap.clear();
            setMethodMap.clear();
            initMethodMap(clsType);
        }
        String fLower = fieldName.toLowerCase();
        if (EMethodType.get == methodType) {
            if (getMethodMap.containsKey(fLower)) return getMethodMap.get(fLower);
        } else {
            if (setMethodMap.containsKey(fLower)) return setMethodMap.get(fLower);
        }
        return null;
    }

    protected ColumnInfo getColumnInfoByText(List<ColumnInfo> columnInfos, String text) {
        ColumnInfo columnInfo = null;
        if (null == columnInfos) return columnInfo;
        if (columnInfos.isEmpty()) return columnInfo;
        if (null == text) return columnInfo;
        if (text.isEmpty()) return columnInfo;
        for (ColumnInfo ci : columnInfos) {
            if (text.equals(ci.getText())) {
                columnInfo = ci;
                break;
            }
            columnInfo = getColumnInfoByText(ci.getChildren(), text);
            if (null != columnInfo) break;
        }
        return columnInfo;
    }

    protected Object convertTo(Object obj, Class<?> type) {
        if (null == obj) return null;
        if (obj.getClass() == type) return obj;
        String val = obj.toString();
        if (String.class == type) {
            return val;
        } else if (int.class == type) {
            if (val.contains(".")) {
                if (String.class != obj.getClass()) {
                    double db = (double) obj;
                    return (int) db;
                }
            }
            return Integer.parseInt(val);
        } else if (boolean.class == type) {
            if (!val.equalsIgnoreCase("true")) val = "false";
            return Boolean.parseBoolean(val);
        } else if (float.class == type) {
            return Float.parseFloat(val);
        } else if (double.class == type) {
            return Double.parseDouble(val);
        } else if ((Date.class == type) || (Calendar.class == type)) {
            return stringToDate(val);
        }
        return val;
    }

    protected TableInfo getTableInfo(String tableOfConfig) throws Exception {
        if (null == tableOfConfig) tableOfConfig = "";
        if (tableOfConfig.isEmpty()) throw new Exception("tableOfConfig can't be empty!");
        String tbLower = tableOfConfig.toLowerCase();
        if (!AbsAnalysisConfig.tableInfoMap.containsKey(tbLower))
            throw new Exception("[" + tableOfConfig + "] is not exit in config of xml.");
        return AbsAnalysisConfig.tableInfoMap.get(tbLower);
    }

    protected Date stringToDate(String dateStr) {
        Date date = new Date();
        boolean success = false;
        try {
            date = new Date(dateStr);
            success = true;
        } catch (Exception ex) {
            //System.out.println("DateFormat.stringToDate.01: "+ex);
        }

        if (!success) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = dateFormat.parse(dateStr);
            } catch (Exception ex) {
                System.out.println("DateFormat.stringToDate.02: " + ex);
            }
        }
        return date;
    }

    private void initMethodMap(Class<?> clsType) {
        Class<?> parentType = clsType.getSuperclass();
        if (Object.class != parentType) {
            initMethodMap(parentType);
        }
        Field[] fields = clsType.getDeclaredFields();
        final String getTag = "get";
        final String setTag = "set";
        for (Field fd : fields) {
            String fieldName = fd.getName();
            String fnLower = fieldName.toLowerCase();
            String fnm = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            /*
            * find getMethod */
            String getMethodName = getTag + fnm;
            Method getMethod = null;
            try {
                getMethod = clsType.getMethod(getMethodName);
            } catch (Exception e) {
                //
            }

            if (null == getMethod) {
                if (boolean.class == fd.getType()) {
                    getMethodName = "is" + fnm;
                    try {
                        getMethod = clsType.getMethod(getMethodName);
                    } catch (Exception e) {
                        //
                    }
                }
            }
            /* if not find getMethod, then find from method collection of object*/
            if (null == getMethod) {
                getMethod = getMethodFromCollection(clsType, getTag, fieldName);
            }

            if (null != getMethod) {
                getMethodMap.put(fnLower, getMethod);
            }

            /*
            * next to find setMethod */
            Method setMethod = null;
            String setMethodName = setTag + fnm;
            try {
                setMethod = clsType.getMethod(setMethodName, fd.getType());
            } catch (Exception e) {
                //
            }
            /* if not find setMethod, then find from method collection of object*/
            if (null == setMethod) {
                setMethod = getMethodFromCollection(clsType, setTag, fieldName);
            }

            if (null != setMethod) {
                setMethodMap.put(fnLower, setMethod);
            }
        }
    }

    private Method getMethodFromCollection(Class<?> clsType, String methodTag, String fieldName) {
        Method[] methods = clsType.getMethods();
        String fnLower = fieldName.toLowerCase();
        Method method = null;
        for (Method md : methods) {
            String mdName = md.getName().toLowerCase();
            if (mdName.startsWith(methodTag) && mdName.contains(fnLower)) {
                method = md;
                break;
            }
        }
        return method;
    }
}
