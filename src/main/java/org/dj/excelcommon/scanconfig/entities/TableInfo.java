package org.dj.excelcommon.scanconfig.entities;

import java.util.List;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/12 18:40
 **/
public class TableInfo {
    private String table;
    private String title;
    private String titleStyle;
    private String headStyle;
    private String bodyStyle;
    private int _dataRowIndex = 0;
    private int _childrenCount = 0;
    private List<ColumnInfo> columnInfos;
    private boolean offAddDataRowIndex = false;

    public int getDataRowIndex() {
        return _dataRowIndex;
    }

    public String getTable() {
        return table;
    }

    public TableInfo setTable(String table) {
        if (null == table) table = "";
        this.table = table;
        offAddDataRowIndex = !table.isEmpty();
        return this;
    }

    public String getTitle() {
        if (null == title) title = "";
        return title;
    }

    public TableInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getChildrenCount() {
        return _childrenCount;
    }

    public TableInfo addChildrenCount() {
        this._childrenCount++;
        return this;
    }

    public List<ColumnInfo> getColumnInfos() {
        return columnInfos;
    }

    public TableInfo setColumnInfos(List<ColumnInfo> columnInfos) {
        this.columnInfos = columnInfos;
        return this;
    }

    public void addDataRowIndex() {
        if (!offAddDataRowIndex) return;
        _dataRowIndex += 1;
    }

    public String getTitleStyle() {
        return titleStyle;
    }

    public TableInfo setTitleStyle(String titleStyle) {
        this.titleStyle = titleStyle;
        return this;
    }

    public String getHeadStyle() {
        return headStyle;
    }

    public TableInfo setHeadStyle(String headStyle) {
        this.headStyle = headStyle;
        return this;
    }

    public String getBodyStyle() {
        return bodyStyle;
    }

    public TableInfo setBodyStyle(String bodyStyle) {
        this.bodyStyle = bodyStyle;
        return this;
    }
}
