package org.dj.excelcommon.excel.importdata;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * @Author DJ
 * @Date 2024/6/14 11:05
 **/
@Component
public class Excel2003ImportService extends AbsExcelImportImpl implements IExcel2003Import {
    public Excel2003ImportService() {
        super();
        workbook = new HSSFWorkbook();
    }
}
