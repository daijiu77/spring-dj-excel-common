package org.dj.excelcommon.excel.importdata;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author DJ
 * @Date 2024/6/14 11:54
 **/
public abstract class AbsExcelImportImpl implements IExcelImport {
    protected Workbook workbook = null;
    private final List<Sheet> sheets = new ArrayList<>();

    @Override
    public IExcelBuilder createBuilder(String tableOfConfig) throws Exception {
        if (null == workbook) {
            workbook = getWorkbook();
        }
        Sheet sheet = workbook.createSheet();
        sheets.add(sheet);
        return new ExcelBuilderImpl(sheet, tableOfConfig);
    }

    @Override
    public boolean save(String filePath) throws Exception {
        if (null == filePath) return false;
        if (filePath.isEmpty()) return false;
        int num = filePath.lastIndexOf(".");
        if (-1 == num) throw new Exception("[" + filePath + "] is not a valid excel file.");
        FileOutputStream fileOutputStream = null;
        boolean success = false;
        boolean isExcel2003 = IExcel2003Import.class.isAssignableFrom(this.getClass());
        String extName = filePath.substring(num + 1);
        if (isExcel2003 && (!extName.equalsIgnoreCase("xls"))) {
            throw new Exception("The file format is wrong, Excel2003 should be in the file format: .xls");
        } else if ((!isExcel2003) && (!extName.equalsIgnoreCase("xlsx"))) {
            throw new Exception("The file format is wrong, Excel2007 should be in the file format: .xlsx");
        }
        try {
            fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);
            success = true;
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != fileOutputStream) {
                fileOutputStream.close();
            }
            close();
        }
        return success;
    }

    @Override
    public InputStream getInputStream(boolean release) throws Exception {
        OutputStream outputStream = getOutputStream(release);
        return new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
    }

    @Override
    public InputStream getInputStream() throws Exception {
        return getInputStream(false);
    }

    @Override
    public OutputStream getOutputStream(boolean release) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        if (release) close();
        return outputStream;
    }

    @Override
    public OutputStream getOutputStream() throws Exception {
        return getOutputStream(false);
    }

    @Override
    public byte[] getBytes(boolean release) throws Exception {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) getOutputStream(release);
        return outputStream.toByteArray();
    }

    @Override
    public byte[] getBytes() throws Exception {
        return getBytes(false);
    }

    @Override
    public void close() throws Exception {
        sheets.clear();
        workbook.close();
        workbook = null;
    }

    protected abstract Workbook getWorkbook();
}
