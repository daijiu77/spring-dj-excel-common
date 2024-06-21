package org.dj.excelcommon;

import org.dj.excelcommon.excel.exportdata.IExcel2003Export;
import org.dj.excelcommon.excel.exportdata.IExcel2007Export;
import org.dj.excelcommon.excel.importdata.IExcel2003Import;
import org.dj.excelcommon.excel.importdata.IExcel2007Import;
import org.dj.excelcommon.excel.importdata.IExcelBuilder;
import org.dj.excelcommon.models.UserInformation;
import org.dj.excelcommon.scanconfig.EnableExcelConfigScan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableExcelConfigScan(configPackages = {"ExcelConfigs"})
class SpringDjExcelCommonApplicationTests {
    @Autowired
    private IExcel2003Export excel2003Export;
    @Autowired
    private IExcel2007Export excel2007Export;

    @Autowired
    private IExcel2007Import excel2007Import;
    @Autowired
    private IExcel2003Import excel2003Import;

    @Test
    void contextLoads() throws Exception {
        String fPath = "D:\\user-info.xls";
        excel2003Export.exportToEntityFromFile(fPath, 1, "UserInfo", UserInformation.class, ((entity, rowIndex) -> {
            System.out.println("row: " + rowIndex + ", data: " + entity.toString());
            return true;
        }));
    }

    @Test
    void Test_GetDataFromExcel2003() throws Exception {
        String fPath = "D:\\user-info.xlsx";
        excel2007Export.exportToEntityFromFile(fPath, 1, "UserInfo", UserInformation.class, ((entity, rowIndex) -> {
            System.out.println("row: " + rowIndex + ", data: " + entity.toString());
            return true;
        }));
    }

    @Test
    void Test_CreateExcel2007() throws Exception {
        IExcelBuilder builder = excel2007Import.createBuilder("UserInfo");
        builder.setSheetName("UserInfo");
        UserInformation userInformation = new UserInformation();
        userInformation.setName("DJ")
                .setSex("man")
                .setAge(21)
                .setAddress("China")
                .setPhone("1231456789")
                .setChinese(99.99f)
                .setPhysics(100f);
        builder.createRow(userInformation, UserInformation.class);

        builder = excel2007Import.createBuilder("UserInfo");
        builder.setSheetName("UserInfo2");
        builder.createRow(userInformation, UserInformation.class);
        builder.createRow(userInformation, UserInformation.class);
        String fPath = "D:\\user-info.xlsx";
        excel2007Import.save(fPath);
        System.out.println("[" + fPath + "] have been saved.");
    }

    @Test
    void Test_CreateExcel2003() throws Exception {
        IExcelBuilder builder = excel2003Import.createBuilder("UserInfo");
        builder.setSheetName("UserInfo");
        UserInformation userInformation = new UserInformation();
        userInformation.setName("DJ")
                .setSex("man")
                .setAge(21)
                .setAddress("China")
                .setPhone("1231456789")
                .setChinese(99.99f)
                .setPhysics(100f);
        builder.createRow(userInformation, UserInformation.class);

        builder = excel2003Import.createBuilder("UserInfo");
        builder.setSheetName("UserInfo2");
        builder.createRow(userInformation, UserInformation.class);
        builder.createRow(userInformation, UserInformation.class);
        String fPath = "D:\\user-info.xls";
        excel2003Import.save(fPath);
        System.out.println("[" + fPath + "] have been saved.");
    }

}
