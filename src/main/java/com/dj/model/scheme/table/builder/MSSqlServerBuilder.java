package com.dj.model.scheme.table.builder;

import com.dj.model.scheme.table.db.AbsDbHelper;
import com.dj.model.scheme.table.db.SqlServerDbHelper;
import com.dj.model.scheme.table.scanner.PropertyInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author DJ insensitive
 * @Description New class
 * @CreateDate 2024/6/19 13:21
 **/
public class MSSqlServerBuilder extends AbsSqlBuilder {
    private FieldTypeImpl fieldType = new FieldTypeImpl();

    public MSSqlServerBuilder(String url, String username, String password) {
        super(url, username, password);
        leftTag = "[";
        rightTag = "]";
    }

    @Override
    protected String GetDatabaseName() {
        String url = AbsDbHelper.datasource_url;
        Pattern pattern = Pattern.compile(";\\s*databasename=([a-zA-z0-9\\-_]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    protected List<String> GetTables(String dbName) {
        String sql = "select name as TABLE_NAME from sysobjects where type='U'";
        return getTables(sql, new SqlServerDbHelper());
    }

    @Override
    protected List<String> GetDbTableFields(String dbName, String tableName) {
        String sql = "select COLUMN_NAME {0},DATA_TYPE {1},CHARACTER_MAXIMUM_LENGTH {2},IS_NULLABLE {3} from INFORMATION_SCHEMA.COLUMNS where TABLE_NAME='{4}'";
        sql = Format(sql, "fName", "fType", "fLength", "notNull", tableName);
        return getDbTableFields(sql, new SqlServerDbHelper());
    }

    @Override
    protected void CreateSql(String tableName, PropertyInfo[] propertyInfos) {
        if (null == tableName) return;
        if (tableName.isEmpty()) return;
        if (null == propertyInfos) return;
        if (0 == propertyInfos.length) return;
        StringBuilder sql = new StringBuilder();
        tableName = getLegalStr(tableName);
        sql.append("create table ").append(tableName);
        sql.append("\r\n").append("(");
        boolean existPrimaryKey = false;
        final String fristTag = "{#NotNull}";
        int num = 0;
        for (PropertyInfo pi : propertyInfos) {
            String key = pi.getName();
            String javaType = pi.getJavaType();
            String sqlType = GetFieldTypeByJavaType(javaType, pi.getLength(), fieldType);
            if (sqlType.isEmpty()) continue;
            String fName = getLegalStr(key);
            sql.append("\r\n").append(fName).append(" ").append(sqlType);
            if (0 == num) {
                sql.append(fristTag);
            }
            if (key.equalsIgnoreCase("id") || pi.getPrimary()) {
                sql.append(" primary key not null");
                existPrimaryKey = true;
            } else if (pi.getNotNull()) {
                sql.append(" not null");
            }
            sql.append(",");
            num++;
        }

        sql.append("\r\n");
        String tagNotNull = "";
        if (!existPrimaryKey) {
            tagNotNull = " primary key not null";
        }
        sql.append("\r\n").append(")");
        String sqlStr = sql.toString();
        sqlStr = sqlStr.replace(fristTag, tagNotNull);

        SqlServerDbHelper sqlServerDbHelper = new SqlServerDbHelper();
        sqlServerDbHelper.executeSql(sqlStr);
        String err = sqlServerDbHelper.getErr();
        sqlServerDbHelper.destroy();
        if (null == err) err = "";
        if (!err.isEmpty()) {
            System.out.println("MSSqlServerBuilder.CreateSql: " + err);
            System.out.println(sql);
        }
    }

    @Override
    protected void AddField(String tableName, PropertyInfo propertyInfo) {
        SqlServerDbHelper sqlServerDbHelper = new SqlServerDbHelper();
        addField(tableName, propertyInfo, sqlServerDbHelper, fieldType);
    }

    class FieldTypeImpl implements FuncFieldType {
        @Override
        public String getFieldType(String javaType, int dataLength) {
            if (javaType.equalsIgnoreCase("uuid")) {
                return "uniqueidentifier";
            } else if (javaType.equalsIgnoreCase("byte[]")) {
                return "image";
            } else if (javaType.equalsIgnoreCase("date") || javaType.equalsIgnoreCase("calendar")) {
                return "datetime";
            } else if (javaType.equalsIgnoreCase("double")) {
                return "money";
            } else if (javaType.equalsIgnoreCase("long")) {
                return "bigint";
            }
            return null;
        }
    }
}
