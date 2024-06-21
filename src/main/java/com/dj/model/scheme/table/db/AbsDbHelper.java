package com.dj.model.scheme.table.db;

import com.dj.model.scheme.table.builder.FuncSqlExecResult;

import java.sql.*;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/19 14:11
 **/
public abstract class AbsDbHelper {
    public static String datasource_url;
    public static String username;
    public static String password;
    private static String driverName;

    private Connection connection;
    private ResultSet rs = null;
    private Statement stmt = null;
    private String err;

    public AbsDbHelper(String driverName) {
        if (null != AbsDbHelper.driverName) {
            if (AbsDbHelper.driverName.equals(driverName)) return;
        }
        try {
            Class.forName(driverName);
            AbsDbHelper.driverName = driverName;
        } catch (Exception e) {
            System.out.println("AbsDbHelper: " + e);
        }
    }

    public ResultSet executeSql(String sql) {
        return executeSql(sql, (result) -> {
        });
    }

    public ResultSet executeSql(String sql, FuncSqlExecResult sqlBuildResult) {
        try {
            err = "";
            if (null != rs) {
                rs.close();
                rs = null;
            }
            if (null == connection) getConnection();
            if (null == connection) return rs;
            if (connection.isClosed()) {
                System.out.println("DataSource information is invalid:");
                System.out.println("datasource.url: " + datasource_url);
                System.out.println("datasource.username: " + username);
                System.out.println("datasource.password: " + password);
                return rs;
            }

            if (null != stmt) {
                if (!stmt.isClosed()) stmt.close();
            }

            stmt = connection.createStatement();
            sql = sql.trim();
            String update = sql.substring(0, "update".length() + 1).toLowerCase();
            String select = sql.substring(0, "select".length() + 1).toLowerCase();
            if (update.equals("update ")) {
                int num = stmt.executeUpdate(sql);
                if (null != sqlBuildResult) sqlBuildResult.ExecResult(num);
            } else if (select.equals("select ")) {
                if (null != rs) {
                    if (!rs.isClosed()) rs.close();
                }
                rs = stmt.executeQuery(sql);
                if (null != sqlBuildResult) sqlBuildResult.ExecResult(rs);
            } else {
                boolean success = stmt.execute(sql);
                if (null != sqlBuildResult) sqlBuildResult.ExecResult(success);
            }
        } catch (SQLException e) {
            System.out.println(e);
            setErr(e.getMessage());
        }
        return rs;
    }

    public void destroy() {
        try {
            if (stmt != null) {
                if (!stmt.isClosed())
                    stmt.close();
            }

        } catch (SQLException e) {
            // e.printStackTrace();
        }

        try {
            if (rs != null) {
                if (!rs.isClosed())
                    rs.close();
            }
        } catch (SQLException e) {
            // e.printStackTrace();
        }

        try {
            if (connection != null) {
                if (!connection.isClosed())
                    connection.close();
            }

        } catch (SQLException e) {
            // e.printStackTrace();
        } finally {
            connection = null;
            stmt = null;
            rs = null;
        }
    }

    private void getConnection() {
        if (null != connection) {
            try {
                if (!connection.isClosed()) return;
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }

        try {
            connection = DriverManager.getConnection(datasource_url, username, password);
        } catch (Exception ex) {
            System.out.println(ex);
            setErr(ex.getMessage());
        }
    }

    public String getErr() {
        return err;
    }

    public AbsDbHelper setErr(String err) {
        this.err = err;
        return this;
    }
}
