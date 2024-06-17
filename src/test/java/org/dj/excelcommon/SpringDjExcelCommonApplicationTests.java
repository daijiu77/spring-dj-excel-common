package org.dj.excelcommon;

import org.dj.excelcommon.excel.exportdata.IExcel2003Export;
import org.dj.excelcommon.excel.importdata.IExcel2007Import;
import org.dj.excelcommon.excel.importdata.IExcelBuilder;
import org.dj.excelcommon.models.UserInfo;
import org.dj.excelcommon.scanconfig.EnableExcelConfigScan;
import org.dj.excelcommon.scanconfig.entities.ColumnInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
@EnableExcelConfigScan(configPackages = {"ExcelConfigs"})
class SpringDjExcelCommonApplicationTests {
    @Autowired
    private IExcel2003Export export;
    @Autowired
    private IExcel2007Import excel2007Import;

    @Test
    void contextLoads() throws Exception {
        List<ColumnInfo> list = new ArrayList<>();
        list.add(new ColumnInfo().setIndex(2).setName("Zs").setAlias("Zs"));
        list.add(new ColumnInfo().setIndex(3).setName("We").setAlias("We"));
        list.add(new ColumnInfo().setIndex(0).setName("GGW").setAlias("GGW"));
        list.add(new ColumnInfo().setIndex(1).setName("QQ").setAlias("QQ"));
        list.sort(new Comparator<ColumnInfo>() {
            @Override
            public int compare(ColumnInfo o1, ColumnInfo o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
        for (ColumnInfo ci : list) {
            System.out.println(ci.toString());
        }
        String fPath = "D:\\user-info.xls";
        //IExcel2003Export excel2003Export = new Excel2003ExportService();
        export.exportToEntityFromFile(fPath, 1, "UserInfo", UserInfo.class, ((entity, rowIndex) -> {
            System.out.println("row: " + rowIndex + ", data: " + entity.toString());
            return true;
        }));
    }

    @Test
    void Test_CreateExcel() throws Exception {
        IExcelBuilder builder = excel2007Import.createBuilder("UserInfo");
        builder.setSheetName("用户信息1");
        UserInfo userInfo = new UserInfo();
        userInfo.setName("DJ")
                .setSex("男")
                .setAge(21)
                .setAddress("深圳")
                .setPhone("15288110805")
                .setChinese(99.99f)
                .setPhysics(100f);
        int n = builder.createRow(userInfo, UserInfo.class);

        builder = excel2007Import.createBuilder("UserInfo");
        builder.setSheetName("用户信息2");
        n = builder.createRow(userInfo, UserInfo.class);
        n += builder.createRow(userInfo, UserInfo.class);
        String fPath = "D:\\DJ.xlsx";
        excel2007Import.save(fPath);
        System.out.println("[" + fPath + "] have been saved.");
    }

}
