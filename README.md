First of all, create a configuration file for the relationship between the Excel file and the data model and data table in your project, if you want to import the data into the Excel file, then you can also do basic styling settings for the Excel file in the configuration file, for example, you can set the column width, text size, color, font, etc. for Excel.

[You can download the JAR package file generated by the compiled code of the resource by clicking on this link](https://github.com/daijiu77/spring-dj-excel-common.jar.git)

The following is a simple configuration file example, and the configuration file name can be named according to the actual business, You can see the sample configuration file in the source code package under the `resources/excelconfigs` path.
[You can click to view the profile](src/main/resources/excelconfigs/Excel-field-mapping.xml)


Configuration description：<br>
`name` - The name of the column in the corresponding data table<br>
`alias` - The alias of the column (corresponding to the property name of the data model)<br>
`text` - Column header text of a table in an Excel file<br>
`columnWidth` - To set the column width of a table in an Excel file, you can also use the `width` property<br>
`allowEmpty` - If set to true, null is allowed, and false is not allowed<br>
`type` - Data type, type range: `string`, `int`, `float`, `double`, `boolean`, `date`<br>
`length` - The allowable length of the data<br>
`headStyle` - Excel file column header style settings, you can set the cell background color, cell foreground color, cell border line line width and border line color, text size, text position in the cell, text font type, text bold<br>
`style` - Each column is styled, and similar to headStyle, this property takes precedence over the headStyle property<br>

Typically the configuration file is located in the project's resources directory:<br>
main<br>
--java<br>
--resources<br>
----excelconfigs<br>
------excel-user-info.xml<br>
------excel-product.xml<br>
----application.yml<br>

Add a @EnableExcelConfigScan annotation to the startup class and specify the XML configuration file directory location<br>
example:<br>
@SpringBootApplicatio<br>
@EnableExcelConfigScan(configPackages = {"excelconfig"})<br>

      public class UserInformationApplication {
          public static void main(String[] args) {
              SpringApplication.run(UserInformationApplication.class, args);
          }
      }

`How to use it?`<br>
This component supports data import and export in two formats: xls and xlsx file formats, in the program, Excel2003 means the suffix is xls file format, Excel2007 means suffix xlsx file format.<br>
`1. Get the data of the Excel file`<br>
>>@Autowired<br>
>>private `IExcel2003Export` excel2003Export;<br>
>>@Autowired<br>
>>private `IExcel2007Import` excel2007Import;<br>

    @Test
    void getDataFromExcel() throws Exception {        
        String fPath = "D:\\user-info.xls";
        //The third parameter value 'UserInfo', corresponds to the value of the table attribute in the xml configuration file
        excel2003Export.exportToEntityFromFile(fPath, "Sheet1", "UserInfo", UserInfo.class, ((entity, rowIndex) -> {
            //Get the data for each row in the Excel file
            System.out.println("row: " + rowIndex + ", data: " + entity.toString());
            return true;
        }));
    }
   
`2. Import the data into an Excel file`<br>
>>@Autowired<br>
>>private `IExcel2003Export` excel2003Export;<br>
>>@Autowired<br>
>>private `IExcel2007Import` excel2007Import;<br>
   
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

Use IExcel2003Export to call the createExcel method:<br>
byte[] data = createExcel(excel2003Import);<br>
<br>
Use IExcel2007Export to call the createExcel method:<br>
byte[] data = createExcel(excel2007Import);
