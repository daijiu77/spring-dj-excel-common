package com.dj.model.scheme.table.db;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/19 15:49
 **/
public class OracleDbHelper extends AbsDbHelper{
    public OracleDbHelper() {
        super("oracle.jdbc.driver.OracleDriver");
    }
}
