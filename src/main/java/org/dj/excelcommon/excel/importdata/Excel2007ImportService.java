package org.dj.excelcommon.excel.importdata;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * @Author DJ
 * @Description 数据导入到 Excel - xlsx
 * @Date 2024/6/14 12:06
 **/
@Component
public class Excel2007ImportService extends AbsExcelImportImpl implements IExcel2007Import {
    @Override
    protected Workbook getWorkbook() {
        return new XSSFWorkbook();
    }
}
