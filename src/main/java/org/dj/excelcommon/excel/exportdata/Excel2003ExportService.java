package org.dj.excelcommon.excel.exportdata;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @Author DJ
 * @Description 从 Excel 获取数据
 * @CreateDate 2024/6/13 13:11
 **/
@Component
public class Excel2003ExportService extends AbsExcelExportImpl implements IExcel2003Export {
    /**
     * @param inputStream file stream from excel.
     * @return Returns an Excel2003 workbook object with the suffix xls
     * */
    @Override
    protected Workbook getWorkbook(InputStream inputStream) throws Exception {
        if (null == inputStream) throw new Exception("inputStream can't be null!");
        return new HSSFWorkbook(inputStream);
    }
}
