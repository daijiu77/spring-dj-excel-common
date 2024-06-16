package org.dj.excelcommon.excel.importdata.entities;

/**
 * @Author DJ
 * @Date 2024/6/14 10:53
 **/
public class HeadInfo {
    private String fieldName;
    private String name;
    private String text;
    private int index;

    public String getFieldName() {
        return fieldName;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return "HeadInfo{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", index=" + index +
                '}';
    }
}
