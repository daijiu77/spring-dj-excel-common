package com.dj.model.scheme.table.db;

/**
 * @Author DJ
 * @Date 2024/6/1 0:13
 **/
public class MySqlDbHelper extends AbsDbHelper {
    public MySqlDbHelper(){
        super("com.mysql.cj.jdbc.Driver");
    }
}
