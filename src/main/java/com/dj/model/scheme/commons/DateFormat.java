package com.dj.model.scheme.commons;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 19:18
 **/
public class DateFormat {
    public static String dateToString(Date date) {
        String dt = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dt = dateFormat.format(date);
        return dt;
    }

    public static Date stringToDate(String dateStr) {
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
                System.out.println("DateFormat.stringToDate.02: "+ex);
            }
        }
        return date;
    }
}
