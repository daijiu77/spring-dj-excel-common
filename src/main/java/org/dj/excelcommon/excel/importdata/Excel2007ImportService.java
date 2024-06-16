package org.dj.excelcommon.excel.importdata;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * @Author DJ
 * @Description 数据导入到 Excel - xlsx
 * @Date 2024/6/14 12:06
 **/
@Component
public class Excel2007ImportService extends AbsExcelImportImpl implements IExcel2007Import {
    public Excel2007ImportService() {
        super();
        workbook = new XSSFWorkbook();
    }
}
