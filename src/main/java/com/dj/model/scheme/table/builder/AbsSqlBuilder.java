package com.dj.model.scheme.table.builder;

import com.dj.model.scheme.table.db.AbsDbHelper;
import com.dj.model.scheme.table.scanner.PropertyInfo;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/10 17:38
 **/
public abstract class AbsSqlBuilder {
    /**
     * dbTableMap - key 为表名小写, value 为表名原字母
     */
    private final Map<String, String> dbTableMap = new HashMap<>();

    /**
     * dbTbFieldMap - key 为表名小写, value 为表所对应的字段集合 fields-Map, fields-Map 的 key 为字段名小写, value为字段名原字母
     */
    private final Map<String, Map<String, String>> dbTbFieldMap = new HashMap<>();

    /**
     * clsPropMap - key 为类名的原字母(区分大小写), value 为属性信息 PropertyInfo 的集合
     */
    private Map<String, PropertyInfo[]> clsPropMap = null;
    private String err;
    private int scanCount = 0;

    protected String leftTag = "";
    protected String rightTag = "";

    public AbsSqlBuilder(String url, String username, String password){
        AbsDbHelper.datasource_url = url;
        AbsDbHelper.username = username;
        AbsDbHelper.password = password;
    }

    public void Exec(Map<String, PropertyInfo[]> clsPropMap) {
        if (null == clsPropMap) return;
        if (clsPropMap.isEmpty()) return;
        String dbName = GetDatabaseName();
        if (null == dbName) return;
        if (dbName.isEmpty()) return;
        List<String> tables = GetTables(dbName);
        if (null == tables) return;
        if (tables.isEmpty()) return;
        for (String tb : tables) {
            String tbLower = tb.toLowerCase();
            dbTableMap.put(tbLower, tb);
            List<String> list = GetDbTableFields(dbName, tb);
            Map<String, String> map = new HashMap<>();
            for (String fName : list) {
                map.put(fName.toLowerCase(), fName);
            }
            dbTbFieldMap.put(tbLower, map);
        }

        scanCount = 0;
        CreateLostTable(clsPropMap, tableName -> {
            scanCount++;
            System.out.println("Has been scanned - " + scanCount + ": " + tableName);
        });
    }

    protected String getErr() {
        String msg = err;
        if (null == msg) msg = "";
        err = "";
        return msg;
    }

    protected AbsSqlBuilder setErr(String err) {
        this.err = err;
        return this;
    }

    private void CreateLostTable(Map<String, PropertyInfo[]> clsPropMap, FuncScannedTable funcScannedTable) {
        if (clsPropMap.isEmpty()) return;
        for (String key : clsPropMap.keySet()) {
            if (null != funcScannedTable) funcScannedTable.table(key);
            String tbLower = key.toLowerCase();
            if (dbTableMap.containsKey(tbLower)) {
                if (dbTbFieldMap.containsKey(tbLower)) {
                    Map<String, String> dbfMap = dbTbFieldMap.get(tbLower);
                    PropertyInfo[] propertyInfos = clsPropMap.get(key);
                    for (PropertyInfo pi : propertyInfos) {
                        String fLower = pi.getName().toLowerCase();
                        if (dbfMap.containsKey(fLower)) continue;
                        AddField(key, pi);
                    }
                }
                continue;
            }
            CreateSql(key, clsPropMap.get(key));
            String err = getErr();
            if (err.isEmpty()) {
                dbTableMap.put(key.toLowerCase(), key);
            }
        }
    }

    protected String getLegalStr(String name) {
        return leftTag + name + rightTag;
    }

    protected String Format(String src, String... args) {
        if (null == args) return src;
        int num = 0;
        for (String item : args) {
            String s1 = "{" + num + "}";
            src = src.replace(s1, item);
            num++;
        }
        return src;
    }

    protected String GetFieldTypeByJavaType(String javaType, int dataLength, FuncFieldType funcFieldType) {
        String typeName = "";
        if (0 == dataLength) dataLength = 200;
        if (javaType.contains(".")) {
            javaType = javaType.substring(javaType.lastIndexOf(".") + 1);
        }
        String jt = javaType.toLowerCase().trim();
        if (null != funcFieldType) {
            typeName = funcFieldType.getFieldType(jt, dataLength);
            if (null == typeName) typeName = "";
            if (!typeName.isEmpty()) return typeName;
        }

        if (jt.equals("string")) {
            typeName = "VARCHAR({0})";
        } else if (jt.equals("char")) {
            typeName = "CHAR({0})";
        } else if (jt.equals("boolean")) {
            typeName = "BIT";
        } else if (jt.equals("short")) {
            typeName = "SMALLINT";
        } else if (jt.equals("long")) {
            typeName = "BIGINT";
        } else if (jt.equals("byte[]")) {
            typeName = "BLOB";
        } else if (jt.equals("byte")) {
            typeName = "TINYINT";
        } else if (jt.equals("date") || jt.equals("time")) {
            typeName = "TIMESTAMP";
        } else if (jt.equals("int") || jt.equals("integer")) {
            typeName = "INT";
        } else if (jt.equals("float")) {
            typeName = "FLOAT";
        } else if (jt.equals("double")) {
            typeName = "DOUBLE";
        }
        if (typeName.isEmpty()) return typeName;
        typeName = typeName.replace("{0}", String.valueOf(dataLength));
        return typeName;
    }

    protected String[] getFieldsFromDb(ResultSet rs) {
        String[] fields = null;
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int len = rsmd.getColumnCount();
            fields = new String[len];
            for (int i = 0; i < len; i++) {
                fields[i] = rsmd.getColumnName(i + 1);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return fields;
    }

    protected void addField(String tableName, PropertyInfo propertyInfo, AbsDbHelper dbHelper, FuncFieldType fieldType){
        StringBuilder sql = new StringBuilder();
        String tbName = getLegalStr(tableName);
        String fName = propertyInfo.getName();
        fName = getLegalStr(fName);
        String javaType = propertyInfo.getJavaType();
        String sqlType = GetFieldTypeByJavaType(javaType, propertyInfo.getLength(), fieldType);

        sql.append("alter table ")
                .append(tbName)
                .append(" add ")
                .append(fName).append(" ")
                .append(sqlType);
        if (propertyInfo.getNotNull()) {
            sql.append(" not null");
        }

        dbHelper.executeSql(sql.toString());
        dbHelper.destroy();
        String err = dbHelper.getErr();
        if (null == err) err = "";
        if (!err.isEmpty()) {
            System.out.println("AbsSqlBuilder.AddField: " + err);
            System.out.println(sql);
        }
    }

    protected List<String> getTables(String sql, AbsDbHelper dbHelper){
        return getFieldValues(sql, dbHelper);
    }

    protected List<String> getDbTableFields(String sql, AbsDbHelper dbHelper){
        return getFieldValues(sql, dbHelper);
    }

    protected abstract String GetDatabaseName();

    protected abstract List<String> GetTables(String dbName);

    protected abstract List<String> GetDbTableFields(String dbName, String tableName);

    protected abstract void CreateSql(String tableName, PropertyInfo[] propertyInfos);

    protected abstract void AddField(String tableName, PropertyInfo propertyInfo);

    private List<String> getFieldValues(String sql, AbsDbHelper dbHelper){
        List<String> list = new ArrayList<>();
        ResultSet rs = dbHelper.executeSql(sql);
        if (null == rs) return list;
        try {
            while (rs.next()) {
                String fv = rs.getString(1);
                if (null == fv) continue;
                if (fv.isEmpty()) continue;
                list.add(fv);
            }
        } catch (Exception e) {
            System.out.println("AbsSqlBuilder.getFieldValues: " + e);
        }
        dbHelper.destroy();
        return list;
    }
    @FunctionalInterface
    private interface FuncScannedTable {
        void table(String tableName);
    }
}
