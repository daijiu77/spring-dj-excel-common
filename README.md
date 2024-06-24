First of all, create a configuration file for the relationship between the Excel file and the data model and data table in your project, if you want to import the data into the Excel file, then you can also do basic styling settings for the Excel file in the configuration file, for example, you can set the column width, text size, color, font, etc. for Excel.

[You can download the JAR package file generated by the compiled code of the resource by clicking on this link](https://github.com/daijiu77/spring-dj-excel-common.jar.git)

The following is a simple configuration file example, and the configuration file name can be named according to the actual business, You can see the sample configuration file in the source code package under the `resources/excelconfigs` path.
[You can click to view the profile](/src/main/resources/ExcelConfigs/Excel-field-mapping.xml)

**FieldMappings** tab attributes description:<br>
`table` - The name of the table, which is used when performing a data import or export in the program<br>
`title` - sheet table. When the data is imported into Excel, the value of the attribute is not empty for several miles, and the value of the attribute will be displayed in the first row and column of the sheet table<br>
`headStyle` - Excel file column header style settings, you can set the cell background color, cell foreground color, cell border line line width and border line color, text size, text position in the cell, text font type, text bold<br>

**column** tab attributes description:<br>
`name` - The name of the column in the corresponding data table<br>
`index` - Ordinal number, set the order in which the columns are displayed in the Excel sheet, and if not set, they are displayed in order from top to bottom in the profile<br>
`alias` - The alias of the column (corresponding to the attribute name of the data model)<br>
`text` - Column header text of a table in an Excel file<br>
`columnWidth` - To set the column width of a table in an Excel file, you can also use the **width** attribute<br>
`allowEmpty` - If set to true, null is allowed, and false is not allowed<br>
`type` - Data type, type range: string, int, float, double, boolean, date<br>
`length` - The allowable length of the data<br>
`style` - Each column is styled, and similar to **headStyle**, this attribute takes precedence over the **headStyle** attribute in the FieldMapping tab<br>
`headStyle` - Set the column header style corresponding to the current column in Excel separately, which takes precedence over the **style** attribute and the **headStyle** attribute in the FieldMapping tab,  you can set the cell background color, cell foreground color, cell border line line width and border line color, text size, text position in the cell, text font type, text bold<br>
`dataStyle` - Sets the data region style for the current column in Excel separately, which takes precedence over the **style** attribute<br>

**Range of style support:**<br>
`color` - Set the font color, only support values within the color gamut<br>
`font-family` - Set the font name<br>
`font-italic` - Sets whether the font is italic, and the text appears in italics when the value is true<br>
`font-weight` - Set the font to be bold, and bold when the value is true<br>
`font-size` - Set the font size<br>
`text-underline` - Sets whether the text needs to be underlined, and underlined if the value is true<br>
`text-align` - The position of the text in the horizontal direction in the cell, the value range: left, center, right<br>
`text-valign` - The position of the text in the vertical direction in the cell, and the value range is: top, center, bottom<br>
`background-color` - Set the background color of the cell, only support values within the color gamut<br>
`border-width` - Set the width of the cell border, the value range is 0~13<br>
`border-color` - Set the color of the cell border，only support values within the color gamut<br>

**The range of color gamuts supported by the system:**<br>
BLACK1, WHITE1, RED1, BRIGHT_GREEN1, BLUE1, YELLOW1, PINK1, TURQUOISE1, BLACK, WHITE, RED, BRIGHT_GREEN, BLUE, YELLOW, PINK, TURQUOISE, DARK_RED, GREEN, DARK_BLUE, DARK_YELLOW, VIOLET, TEAL, GREY_25_PERCENT, GREY_50_PERCENT, CORNFLOWER_BLUE, MAROON, LEMON_CHIFFON, LIGHT_TURQUOISE1, ORCHID, CORAL, ROYAL_BLUE, LIGHT_CORNFLOWER_BLUE, SKY_BLUE, LIGHT_TURQUOISE, LIGHT_GREEN, LIGHT_YELLOW, PALE_BLUE, ROSE, LAVENDER, TAN, LIGHT_BLUE, AQUA, LIME, GOLD, LIGHT_ORANGE, ORANGE, BLUE_GREY, GREY_40_PERCENT, DARK_TEAL, SEA_GREEN, DARK_GREEN, OLIVE_GREEN, BROWN, PLUM, INDIGO, GREY_80_PERCENT<br>

Typically the configuration file is located in the project's resources directory:<br>
main<br>
--java<br>
--resources<br>
----excelconfigs<br>
------excel-user-info.xml<br>
------excel-product.xml<br>
----application.yml<br>

Add a **@EnableExcelConfigScan** annotation to the startup class and specify the XML configuration file directory location<br>
example:<br>

      @SpringBootApplicatio
      @EnableExcelConfigScan(configPackages = {"excelconfig"})
      public class UserInformationApplication {
          public static void main(String[] args) {
              SpringApplication.run(UserInformationApplication.class, args);
          }
      }

`How to use it?`<br>
This component supports data import and export in two formats: xls and xlsx file formats, in the program, Excel2003 means the suffix is xls file format, Excel2007 means suffix xlsx file format.<br>
`1. Get the data of the Excel file`

    @Autowired
    private IExcel2003Export excel2003Export;
    @Autowired
    private IExcel2007Import excel2007Export;

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
   
`2. Import the data into an Excel file`

    @Autowired
    private IExcel2003Import excel2003Import;
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
            //Here's how to get the UserInfo data from the database and import it into the newly created sheet table
            List<UserInfoQueryDTO> dtos = findUserInfoByName("allan");
            builder.createRows(dtos, UserInfoQueryDTO.class);

            byte[] datas = excelImport.getBytes();
            //You can also choose to save the created Excel file to a specified disk location
            //excelImport.save("D:\\user-info." + extName);
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

    //Use IExcel2003Import to call the createExcel method
    byte[] data = createExcel(excel2003Import);

    //Use IExcel2007Import to call the createExcel method
    byte[] data = createExcel(excel2007Import);

[You can click on the link to view the use cases in the source code](/src/test/java/org/dj/excelcommon/SpringDjExcelCommonApplicationTests.java)