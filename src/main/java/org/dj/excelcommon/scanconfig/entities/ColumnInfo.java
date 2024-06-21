package org.dj.excelcommon.scanconfig.entities;

import java.util.List;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/12 18:30
 **/
public class ColumnInfo {
    /**
     * index - 在 excel 中的排列序号
     */
    private int index = 0;
    /**
     * name - 在数据表中的映射名称(对应数据表中的字段名称)
     */
    private String name;
    /**
     * alias - 在程序中使用的名称(对应数据实体的属性名称)
     */
    private String alias;
    /**
     * text - 在 excel 中的列头文本
     */
    private String text;
    /**
     * style - 在 excel 中当前列的样式设置(该样式作用于列头和数据),优先级大于 TableInfo 中的 headStyle 属性
     */
    private String style;
    /**
     * headStyle - 在 excel 中当前列的列头样式设置,优先级大于 TableInfo 中的 headStyle 属性及当前列的 style 属性
     * */
    private String headStyle;
    /**
     * dataStyle - 在 excel 中当前列对应的数据单元样式设置, 优先级大于 style 属性
     * */
    private String dataStyle;
    /**
     * allowEmpty - 允许该列数据为空值
     */
    private boolean allowEmpty;
    /**
     * type - 数据类型
     */
    private String type;
    /**
     * length - 是字符串的情况,限制字符长度
     */
    private int length;
    /**
     * columnWidth - 在 excel 中当前列的宽度
     */
    private int columnWidth;
    /**
     * width - 等同于 columnWidth
     */
    private int width;
    /**
     * children - 在 excel 中有子项，表示当前列头需要做跨列处理; 而无子项的列头需要做跨行处理
     */
    private List<ColumnInfo> children;

    private final int _widthVector = 20;

    private TableInfo tableInfo;

    public int getIndex() {
        return index;
    }

    public ColumnInfo setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getName() {
        if (null == name) name = "";
        return name;
    }

    public ColumnInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getAlias() {
        if (null == alias) alias = "";
        return alias;
    }

    public ColumnInfo setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getText() {
        return text;
    }

    public ColumnInfo setText(String text) {
        this.text = text;
        return this;
    }

    public boolean getAllowEmpty() {
        return allowEmpty;
    }

    public ColumnInfo setAllowEmpty(boolean allowEmpty) {
        this.allowEmpty = allowEmpty;
        return this;
    }

    public String getType() {
        return type;
    }

    public ColumnInfo setType(String type) {
        this.type = type;
        return this;
    }

    public int getLength() {
        return length;
    }

    public ColumnInfo setLength(int length) {
        this.length = length;
        return this;
    }

    public int getColumnWidth() {
        return columnWidth * _widthVector;
    }

    public ColumnInfo setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
        this.width = columnWidth;
        return this;
    }

    public int getWidth() {
        return width * _widthVector;
    }

    public ColumnInfo setWidth(int width) {
        this.width = width;
        this.columnWidth = width;
        return this;
    }

    public List<ColumnInfo> getChildren() {
        return children;
    }

    public ColumnInfo setChildren(List<ColumnInfo> children) {
        this.children = children;
        return this;
    }

    public String getStyle() {
        return style;
    }

    public ColumnInfo setStyle(String style) {
        this.style = style;
        return this;
    }

    public String getHeadStyle() {
        return headStyle;
    }

    public ColumnInfo setHeadStyle(String headStyle) {
        this.headStyle = headStyle;
        return this;
    }

    public String getDataStyle() {
        return dataStyle;
    }

    public ColumnInfo setDataStyle(String dataStyle) {
        this.dataStyle = dataStyle;
        return this;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public ColumnInfo setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        if (0 == index) index = tableInfo.getChildrenCount();
        tableInfo.addChildrenCount();
        return this;
    }

    @Override
    public String toString() {
        return "ColumnInfo{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", text='" + text + '\'' +
                ", allowEmpty=" + allowEmpty +
                ", type='" + type + '\'' +
                ", length=" + length +
                ", columnWidth=" + columnWidth +
                '}';
    }
}
