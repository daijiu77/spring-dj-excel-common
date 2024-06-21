package org.dj.excelcommon.excel.exportdata;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @Author DJ
 * @Description 从 Excel 获取数据
 * @Date 2024/6/13 23:57
 **/
@Component
public class Excel2007ExportService extends AbsExcelExportImpl implements IExcel2007Export {
    /**
     * @param inputStream file stream from excel.
     * @return Returns an Excel2007 workbook object with the suffix xlsx
     */
    @Override
    protected Workbook getWorkbook(InputStream inputStream) throws Exception {
        if (null == inputStream) throw new Exception("inputStream can't be null!");
        return new XSSFWorkbook(inputStream);
    }
}
