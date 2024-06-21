package com.dj.model.scheme.table.builder;

import com.dj.model.scheme.table.db.MySqlDbHelper;
import com.dj.model.scheme.table.scanner.PropertyInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author DJ
 * @Date 2024/6/9 2:54
 **/
public class MySqlBuilder extends AbsSqlBuilder {
    private FieldTypeImpl fieldType = new FieldTypeImpl();

    public MySqlBuilder(String url, String username, String password) {
        super(url, username, password);
        leftTag = "`";
        rightTag = "`";
    }

    @Override
    protected String GetDatabaseName() {
        String url = MySqlDbHelper.datasource_url;
        String rg = "([a-zA-z0-9\\-]+)\\?";
        Pattern pattern = Pattern.compile(rg);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    protected List<String> GetTables(String dbName) {
        String sql = "select TABLE_NAME from information_schema.tables where LOWER(TABLE_SCHEMA)='{0}' and LOWER(TABLE_SCHEMA)<>'mysql' and LOWER(ENGINE)='innodb'";
        sql = sql.replace("{0}", dbName);
        return getTables(sql, new MySqlDbHelper());
    }

    @Override
    protected List<String> GetDbTableFields(String dbName, String tableName) {
        String sql = "select COLUMN_NAME {0},DATA_TYPE {1},CHARACTER_MAXIMUM_LENGTH {2},IS_NULLABLE {3} from INFORMATION_SCHEMA.COLUMNS where TABLE_SCHEMA='{4}' AND TABLE_NAME='{5}'";
        sql = Format(sql, "fName", "fType", "fLength", "notNull", dbName, tableName);
        return getDbTableFields(sql, new MySqlDbHelper());
    }

    @Override
    protected void CreateSql(String tableName, PropertyInfo[] propertyInfos) {
        if (null == tableName) return;
        if (tableName.isEmpty()) return;
        if (null == propertyInfos) return;
        if (0 == propertyInfos.length) return;
        StringBuilder primaryKeys = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        tableName = getLegalStr(tableName);
        sql.append("create table ").append(tableName);
        sql.append("\r\n").append("(");
        String fristField = "";
        final String fristTag = "{#NotNull}";
        int num = 0;
        for (PropertyInfo pi : propertyInfos) {
            String key = pi.getName();
            if (0 == num) fristField = key;
            String javaType = pi.getJavaType();
            String sqlType = GetFieldTypeByJavaType(javaType, pi.getLength(), fieldType);
            if (sqlType.isEmpty()) continue;
            String fName = getLegalStr(key);
            sql.append("\r\n").append(fName).append(" ").append(sqlType);
            if (0 == num) {
                fristField = key;
                sql.append(fristTag);
            }
            if (key.equalsIgnoreCase("id") || pi.getPrimary()) {
                sql.append(" not null");
                if (primaryKeys.isEmpty()) {
                    primaryKeys.append("primary key(").append(fName).append(")");
                } else {
                    primaryKeys.append(",\r\n").append("primary key(").append(fName).append(")");
                }
            } else if (pi.getNotNull()) {
                sql.append(" not null");
            }
            sql.append(",");
            num++;
        }

        sql.append("\r\n");
        String tagNotNull = "";
        if (primaryKeys.isEmpty()) {
            fristField = getLegalStr(fristField);
            sql.append("primary key(").append(fristField).append(")");
            tagNotNull = " not null";
        } else {
            sql.append(primaryKeys);
        }
        sql.append("\r\n").append(") character set utf8;");
        String sqlStr = sql.toString();
        sqlStr = sqlStr.replace(fristTag, tagNotNull);

        MySqlDbHelper mySqlDbHelper = new MySqlDbHelper();
        mySqlDbHelper.executeSql(sqlStr);
        String err = mySqlDbHelper.getErr();
        mySqlDbHelper.destroy();
        if (null == err) err = "";
        if (!err.isEmpty()) {
            System.out.println("MySqlBuilder.CreateSql: " + err);
            System.out.println(sql);
        }
    }

    @Override
    protected void AddField(String tableName, PropertyInfo propertyInfo) {
        MySqlDbHelper mySqlDbHelper = new MySqlDbHelper();
        addField(tableName, propertyInfo, mySqlDbHelper, fieldType);
    }

    class FieldTypeImpl implements FuncFieldType {
        @Override
        public String getFieldType(String javaType, int dataLength) {
            if (javaType.equalsIgnoreCase("uuid")) {
                return "varchar(36)";
            }
            return null;
        }
    }

}
