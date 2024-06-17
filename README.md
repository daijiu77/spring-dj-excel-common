First of all, create a configuration file for the relationship between the Excel file and the data model and data table in your project, if you want to import the data into the Excel file, then you can also do basic styling settings for the Excel file in the configuration file, for example, you can set the column width, text size, color, font, etc. for Excel.

The following is a simple configuration file example, and the configuration file name can be named according to the actual business:
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<FieldMappings table="UserInfo"
               title="User information"
               headStyle="text-align:center;text-valign:center;background-color:green;color:white;border-width:1px;border-color:yellow;">
    <column alias="id" allowEmpty="true" columnWidth="500" length="36" name="id" text="id" type="string" index="1" style="" />
    <column alias="order_by" allowEmpty="false" columnWidth="100" name="order_by" text="Serial" type="int" index="2" style="" />
    <column alias="uid" allowEmpty="false" columnWidth="100" length="50" name="uid" text="Account" type="string" index="3" style=""/>
    <column alias="pwd" allowEmpty="false" columnWidth="100" length="50" name="pwd" text="Password" type="string" index="4" style=""/>
    <column alias="user_type" allowEmpty="true" columnWidth="120" length="0" name="user_type" text="UserType" index="5" style=""/>
    <column alias="name" allowEmpty="false" columnWidth="100" length="5" name="name" text="Name" type="string" index="6" style=""/>
    <column alias="gender" allowEmpty="false" columnWidth="80" length="1" name="gender" text="Gender" type="int" index="7" style=""/>
    <column alias="age" allowEmpty="true" columnWidth="80" name="age" text="Age" type="int" index="8" style=""/>
    <column alias="phone" allowEmpty="false" columnWidth="180" length="20" name="phone" text="Phone" type="string" index="9" style=""/>
    <!--If the column has children, it means that the parent column needs to be displayed across columns-->
    <column alias="course" name="course" text="Course" index="10">
        <column alias="chemistry" allowEmpty="true" columnWidth="120" length="0" name="chinese" text="Chemistry" type="float" index="2" style=""/>
        <column alias="physics" allowEmpty="true" columnWidth="120" length="0" name="physics" text="Physics" type="float" index="1" style=""/>
    </column>
    <column alias="email" allowEmpty="true" columnWidth="200" length="200" name="email" text="Email" type="string" index="11" style=""/>
    <column alias="address" allowEmpty="true" width="300" length="100" name="address" text="Address" type="string" index="12" style=""/>
    <column alias="is_enabled" allowEmpty="false" columnWidth="100" name="is_enabled" text="Enable" type="boolean" index="13" style="" />
    <column alias="create_time" allowEmpty="true" columnWidth="280" name="create_time" text="CreateDate" type="date" index="14" style="" />
</FieldMappings>

Configuration description：
    `name` - The name of the column in the corresponding data table< /br>
    `alias` - The alias of the column (corresponding to the attribute name of the data model)
    `text` - Column header text of a table in an Excel file
    `columnWidth` - To set the column width of a table in an Excel file, you can also use the `width` property
    `allowEmpty` - If set to true, null is allowed, and false is not allowed
`type` - Data type, type range: `string`, `int`, `float`, `double`, `boolean`, `date`
`length` - The allowable length of the data
`headStyle` - Excel file column header style settings, you can set the cell background color, cell foreground color, cell border line line width and border line color, text size, text position in the cell, text font type, text bold
`style` - Each column is styled, and similar to headStyle, this property takes precedence over the headStyle property

Typically the configuration file is located in the project's resources directory:
main
  java
  resources
    excelconfigs
      excel-user-info.xml
      excel-product.xml
    application.yml

Add a @EnableExcelConfigScan annotation to the startup class and specify the XML configuration file directory location
example:
@SpringBootApplicatio
@EnableExcelConfigScan(configPackages = {"excelconfig"})
public class UserInformationApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserInformationApplication.class, args);
    }
}

`How to use it?`
This component supports data import and export in two formats: xls and xlsx file formats, in the program, Excel2003 means the suffix is xls file format, Excel2007 means suffix xlsx file format.
`1. Get the data of the Excel file`
    @Autowired
    private IExcel2003Export excel2003Export;
    @Autowired
    private IExcel2007Import excel2007Import;

    @Test
    void getDataFromExcel() throws Exception {        
        String fPath = "D:\\user-info.xls";
        excel2003Export.exportToEntityFromFile(fPath, "Sheet1", "UserInfo", UserInfo.class, ((entity, rowIndex) -> {
            //获取 Excel 中每一行的数据
            System.out.println("row: " + rowIndex + ", data: " + entity.toString());
            return true;
        }));
    }
   
`2. Import the data into an Excel file`
    @Autowired
    private IExcel2003Export excel2003Export;
    @Autowired
    private IExcel2007Import excel2007Import;
   
    private byte[] createExcel(IExcelImport excelImport) {
        String extName = "xls";
        if (IExcel2007Import.class.isAssignableFrom(excelImport.getClass())) extName = "xlsx";   
        try {
            //Getting an IExcelBuilder interface object is equivalent to creating a new sheet form
            IExcelBuilder builder = excelImport.createBuilder("UserInfo");
            UserInfo userInfo = new UserInfo();
            userInfo.setName("DJ").setAge(18).setPhone("1231456789").setGender(1).setUid("admin")
                    .setPwd("admin").setEmail("dj@qq.com").setAddress("China")
                    .setOrder_by(1).setIs_enabled(true);
            builder.createRow(userInfo, UserInfo.class);

            //Here we get another IExcelBuilder interface object, and we create a new sheet again
            builder = excelImport.createBuilder("UserInfo");
            builder.setSheetName("UserInfoQueryDTO");
            //Here's how to get the UserInfo data from the database and import it into the newly created sheet form
            List<UserInfoQueryDTO> dtos = findUserInfoByName("allan");
            builder.createRows(dtos, UserInfoQueryDTO.class);

            byte[] datas = excelImport.getBytes();
            //You can also choose to save the created Excel file to a specified disk location
            //excelImport.save("D:\\user-info.xlsx");
            return datas;
        } catch (Exception e) {
            System.out.println("Excel import exception: " + e);
        } finally {
            try {
                excelImport.close();
            } catch (Exception e) {
                //
            }
        }
        return new byte[0];
    }

    Use IExcel2003Export to call the createExcel method:
    byte[] data = createExcel(excel2003Import);

    Use IExcel2007Export to call the createExcel method:
    byte[] data = createExcel(excel2007Import);


