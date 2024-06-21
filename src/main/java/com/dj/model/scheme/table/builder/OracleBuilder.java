package com.dj.model.scheme.table.builder;

import com.dj.model.scheme.table.db.OracleDbHelper;
import com.dj.model.scheme.table.db.SqlServerDbHelper;
import com.dj.model.scheme.table.scanner.PropertyInfo;

import java.util.List;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/19 15:51
 **/
public class OracleBuilder extends AbsSqlBuilder {
    private FieldTypeImpl fieldType = new FieldTypeImpl();

    public OracleBuilder(String url, String username, String password) {
        super(url, username, password);
        leftTag = "\"";
        rightTag = "\"";
    }

    @Override
    protected String GetDatabaseName() {
        return null;
    }

    @Override
    protected List<String> GetTables(String dbName) {
        String sql = "SELECT TABLE_NAME FROM user_tables";
        return getTables(sql, new OracleDbHelper());
    }

    @Override
    protected List<String> GetDbTableFields(String dbName, String tableName) {
        String sql = "select COLUMN_NAME {0},DATA_TYPE {1},DATA_LENGTH {2},NULLABLE {3} from USER_TAB_COLUMNS where TABLE_NAME='{4}'";
        sql = Format(sql, "fName", "fType", "fLength", "notNull", tableName);
        return getDbTableFields(sql, new OracleDbHelper());
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
            System.out.println("OracleBuilder.CreateSql: " + err);
            System.out.println(sql);
        }
    }

    @Override
    protected void AddField(String tableName, PropertyInfo propertyInfo) {
        OracleDbHelper oracleDbHelper = new OracleDbHelper();
        addField(tableName, propertyInfo, oracleDbHelper, fieldType);
    }

    private class FieldTypeImpl implements FuncFieldType {
        @Override
        public String getFieldType(String javaType, int dataLength) {
            if (javaType.equalsIgnoreCase("uuid")) {
                return "varchar(36)";
            } else if (javaType.equalsIgnoreCase("byte[]")) {
                return "varbinary(" + dataLength + ")";
            } else if (javaType.equalsIgnoreCase("date") || javaType.equalsIgnoreCase("calendar")) {
                return "datetime";
            } else if (javaType.equalsIgnoreCase("double")) {
                return "money";
            } else if (javaType.equalsIgnoreCase("long")) {
                return "bigint";
            } else if (javaType.equalsIgnoreCase("boolean")) {
                return "char(1)";
            }
            return null;
        }
    }
}
