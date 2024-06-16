package org.dj.excelcommon.excel.importdata;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.dj.excelcommon.excel.AbsExcelService;
import org.dj.excelcommon.scanconfig.entities.ColumnInfo;
import org.dj.excelcommon.scanconfig.entities.TableInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author DJ
 * @Date 2024/6/14 12:40
 **/
public abstract class AbsExcelImportService extends AbsExcelService {
    protected TableInfo tableInfo = null;
    /**
     * colorsMap - key: 颜色名称(小写), value: IndexedColors 枚举值
     */
    private Map<String, IndexedColors> colorsMap = new HashMap<>();
    /**
     * borderStyleMap - key: 单元格边框粗度数据值, value: 边框粗度对应枚举
     */
    private Map<Integer, BorderStyle> borderStyleMap = new HashMap<>();

    public AbsExcelImportService() {
        StringBuilder clrs = new StringBuilder();
        for (IndexedColors item : IndexedColors.values()) {
            colorsMap.put(item.name().toLowerCase(), item);
            clrs.append(",").append(item.name());
        }
        System.out.println("Excel style color related settings, which are supported by the system：");
        System.out.println(clrs);
        System.out.println(" ");
        for (BorderStyle bs : BorderStyle.values()) {
            borderStyleMap.put((int) bs.getCode(), bs);
        }
    }

    protected void initExcelHead(Sheet sheet, String tableOfConfig) throws Exception {
        tableInfo = getTableInfo(tableOfConfig);
        String title = tableInfo.getTitle();
        Row row = null;
        Cell cell = null;
        int rowIndex = 0;
        boolean rowspan = false;
        if (!title.isEmpty()) {
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue(title);
            setCellStyle(cell, tableInfo.getTitleStyle());
            rowspan = 3 == tableInfo.getDataRowIndex();
            rowIndex++;
        } else {
            rowspan = 2 == tableInfo.getDataRowIndex();
        }
        List<ColumnInfo> columnInfos = tableInfo.getColumnInfos();
        row = sheet.createRow(rowIndex);
        int colIndex = 0;
        int colIndex1 = 0;
        for (ColumnInfo ci : columnInfos) {
            cell = row.getCell(colIndex);
            if (null == cell) cell = row.createCell(colIndex);
            cell.setCellValue(ci.getText());
            setCellStyle(cell, tableInfo.getHeadStyle());
            setCellStyle(cell, ci.getStyle());
            colIndex1 = colIndex;
            if (rowspan) {
                int[] arr = new int[]{colIndex};
                initHeadChilds(sheet, ci, rowIndex, arr);
                colIndex1 = arr[0];
            }

            if (colIndex1 != colIndex) {
                colIndex = colIndex1;
            } else {
                if (0 < ci.getColumnWidth()) {
                    sheet.setColumnWidth(colIndex, ci.getColumnWidth());
                }
                colIndex++;
            }
        }
    }

    protected void setCellStyle(Cell cell, String style) {
        if (null == style) return;
        if (style.isEmpty()) return;
        Workbook workbook = getWorkbook();
        if (null == workbook) return;
        CellStyle cellStyle = workbook.createCellStyle();
        String[] styles = new String[]{style};
        Font font = getFont(styles);
        if (null != font) {
            cellStyle.setFont(font);
        }
        String s1 = styles[0];
        Pattern pattern = Pattern.compile("([a-zA-Z0-9\\-_]+)\\s*:\\s*([a-zA-Z0-9\\-_]+)");
        Matcher matcher = pattern.matcher(s1);
        while (matcher.find()) {
            String attrName = matcher.group(1);
            String attrValue = matcher.group(2);
            setNormalStyle(cellStyle, attrName, attrValue);
            s1 = s1.replace(matcher.group(0), "");
            matcher = pattern.matcher(s1);
        }
        cell.setCellStyle(cellStyle);
    }

    protected void foreachColumnInfos(List<ColumnInfo> columnInfos, FuncForeachColumnInfos funcForeachColumnInfos) throws Exception {
        if (null == columnInfos) return;
        for (ColumnInfo ci : columnInfos) {
            if (null != ci.getChildren()) {
                if (!ci.getChildren().isEmpty()) {
                    foreachColumnInfos(ci.getChildren(), funcForeachColumnInfos);
                    continue;
                }
            }
            funcForeachColumnInfos.foreach(ci);
        }
    }

    protected abstract Workbook getWorkbook();

    private void initHeadChilds(Sheet sheet, ColumnInfo ci, int rowIndex, int[] colIndexs) {
        int colIndex = colIndexs[0];
        boolean isEmptyChildren = false;
        if (null == ci.getChildren()) {
            isEmptyChildren = true;
        } else if (ci.getChildren().isEmpty()) {
            isEmptyChildren = true;
        }

        Cell cell = null;
        int rowIndex1 = rowIndex + 1;
        if (isEmptyChildren) {
            mergeRow(sheet, colIndex, rowIndex, rowIndex1);
            Row row1 = sheet.getRow(rowIndex1);
            if (null == row1) row1 = sheet.createRow(rowIndex1);
            cell = row1.getCell(colIndex);
            if (null == cell) cell = row1.createCell(colIndex);
            setCellStyle(cell, tableInfo.getHeadStyle());
            setCellStyle(cell, ci.getStyle());
            return;
        }

        List<ColumnInfo> children = ci.getChildren();
        int colStartIndex = colIndex;
        int colEndIndex = colStartIndex + children.size() - 1;
        mergeColumn(sheet, rowIndex, colStartIndex, colEndIndex);
        Row childRow = sheet.getRow(rowIndex1);
        if (null == childRow) {
            childRow = sheet.createRow(rowIndex1);
        }
        for (ColumnInfo child : children) {
            cell = childRow.getCell(colStartIndex);
            if (null == cell) cell = childRow.createCell(colStartIndex);
            cell.setCellValue(child.getText());
            setCellStyle(cell, tableInfo.getHeadStyle());
            setCellStyle(cell, child.getStyle());
            if (0 < child.getColumnWidth()) {
                sheet.setColumnWidth(colStartIndex, child.getColumnWidth());
            }
            colStartIndex++;
        }
        colIndexs[0] = colStartIndex;
    }

    private Font getFont(String[] styles) {
        if (null == styles) return null;
        if (0 == styles.length) return null;
        Workbook workbook = getWorkbook();
        if (null == workbook) return null;
        String s1 = styles[0];
        Pattern pattern = Pattern.compile("[\\s;]((font-[a-zA-Z]+)|(text-decoration)|(color))\\s*:\\s*([a-zA-Z0-9\\-_]+)");
        Matcher matcher = pattern.matcher(s1);
        Font font = null;
        while (matcher.find()) {
            String fontAttr = matcher.group(2);
            String textAttr = matcher.group(3);
            String colorAttr = matcher.group(4);
            String attrVal = matcher.group(5);
            if (null == font) font = workbook.createFont();
            setFontStyle(font, fontAttr, attrVal);
            setFontStyle(font, textAttr, attrVal);
            setFontStyle(font, colorAttr, attrVal);
            s1 = s1.replace(matcher.group(0), "");
            matcher = pattern.matcher(s1);
        }
        styles[0] = s1;
        return font;
    }

    private void setNormalStyle(CellStyle style, String attrName, String attrValue) {
        if (null == style) return;
        if (null == attrName) return;
        if (attrName.isEmpty()) return;
        attrName = attrName.toLowerCase();
        if (attrName.equals("text-align")) {
            if (attrValue.isEmpty()) attrValue = "left";
            attrValue = attrValue.toLowerCase();
            if (attrValue.equals("center")) {
                style.setAlignment(HorizontalAlignment.CENTER);
            } else if (attrValue.equals("left")) {
                style.setAlignment(HorizontalAlignment.LEFT);
            } else if (attrValue.equals("right")) {
                style.setAlignment(HorizontalAlignment.RIGHT);
            }
        } else if (attrName.equals("text-valign")) {
            if (attrValue.isEmpty()) attrValue = "top";
            attrValue = attrValue.toLowerCase();
            if (attrValue.equals("center")) {
                style.setVerticalAlignment(VerticalAlignment.CENTER);
            } else if (attrValue.equals("top")) {
                style.setVerticalAlignment(VerticalAlignment.TOP);
            } else if (attrValue.equals("bottom")) {
                style.setVerticalAlignment(VerticalAlignment.BOTTOM);
            }
        } else if (attrName.equals("background-color")) {
            attrValue = attrValue.toLowerCase();
            if (!colorsMap.containsKey(attrValue)) return;
            style.setFillForegroundColor(colorsMap.get(attrValue).getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if (attrName.equals("border-width")) {
            if (null == attrValue) attrValue = "";
            String s1 = getStyleAttrNumValue(attrValue);
            if (s1.isEmpty()) s1 = "0";
            int num = Integer.parseInt(s1);
            if (0 == num) return;
            if (!borderStyleMap.containsKey(num)) return;
            BorderStyle borderStyle = borderStyleMap.get(num);
            style.setBorderTop(borderStyle);
            style.setBorderBottom(borderStyle);
            style.setBorderLeft(borderStyle);
            style.setBorderRight(borderStyle);
        } else if (attrName.equals("border-color")) {
            attrValue = attrValue.toLowerCase();
            if (!colorsMap.containsKey(attrValue)) return;
            short clr = colorsMap.get(attrValue).getIndex();
            style.setTopBorderColor(clr);
            style.setBottomBorderColor(clr);
            style.setLeftBorderColor(clr);
            style.setRightBorderColor(clr);
        }
    }

    private void setFontStyle(Font font, String attrName, String attrValue) {
        if (null == attrName) return;
        if (attrName.isEmpty()) return;
        if (null == attrValue) attrValue = "";
        attrName = attrName.toLowerCase();
        if (attrName.equals("color")) {
            attrValue = attrValue.toLowerCase();
            if (!colorsMap.containsKey(attrValue)) return;
            font.setColor(colorsMap.get(attrValue).getIndex());
        } else if (attrName.equals("font-family")) {
            if (attrValue.isEmpty()) attrValue = "宋体";
            font.setFontName(attrValue);
        } else if (attrName.equals("font-style")) {
            if (attrValue.isEmpty()) {
                font.setItalic(false);
            } else {
                font.setItalic(true);
            }
        } else if (attrName.equals("font-weight")) {
            if (!attrValue.isEmpty()) attrValue = attrValue.toLowerCase();
            if (attrValue.equals("bold")) {
                font.setBold(true);
            } else {
                font.setBold(false);
            }
        } else if (attrName.equals("font-size")) {
            if (attrValue.isEmpty()) return;
            String s1 = getStyleAttrNumValue(attrValue);
            if (s1.isEmpty()) s1 = "14";
            try {
                short h = Short.parseShort(s1);
                font.setFontHeight(h);
            } catch (Exception e) {
                //
            }
        } else if (attrName.equals("text-decoration")) {
            if (attrValue.isEmpty()) attrValue = "none";
            if (attrValue.equals("underline")) {
                font.setUnderline(Font.U_SINGLE);
            }
        }
    }

    private String getStyleAttrNumValue(String attrValue) {
        String val = "";
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(attrValue);
        if (!matcher.find()) return val;
        val = matcher.group(0);
        return val;
    }

    private void mergeColumn(Sheet sheet, int rowIndex, int colStartIndex, int colEndIndex) {
        CellRangeAddress region = new CellRangeAddress(rowIndex, rowIndex, colStartIndex, colEndIndex);
        sheet.addMergedRegion(region);
    }

    private void mergeRow(Sheet sheet, int colIndex, int rowStartIndex, int rowEndIndex) {
        CellRangeAddress region = new CellRangeAddress(rowStartIndex, rowEndIndex, colIndex, colIndex);
        sheet.addMergedRegion(region);
    }

    @FunctionalInterface
    protected interface FuncForeachColumnInfos {
        void foreach(ColumnInfo columnInfo) throws Exception;
    }
}
