package com.dj.model.scheme.table.db;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/19 13:49
 **/
public class SqlServerDbHelper extends AbsDbHelper {
    public SqlServerDbHelper() {
        super("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }
}
