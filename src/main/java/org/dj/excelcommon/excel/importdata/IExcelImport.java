package org.dj.excelcommon.excel.importdata;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author DJ
 * @Date 2024/6/14 10:49
 **/
public interface IExcelImport {
    /**
     * @param tableOfConfig 对应 xml 配置文件里 FieldMappings 标签的 table 属性值[该值必须是唯一的]
     * */
    IExcelBuilder createBuilder(String tableOfConfig) throws Exception;
    /**
     * 把创建的 excel 数据保存为文件,并释放资源
     * @param filePath excel 的方法路径
     * @return 返回 true 表示文件保存成功
     * */
    boolean save(String filePath) throws Exception;
    /**
     * @param release 是否释放资源, 为 true(释放资源)
     * @return 返回 InputStream 数据对象, 如果参数设置为 false, 需要执行 close 方法释放资源
     * */
    InputStream getInputStream(boolean release) throws Exception;
    /**
     * @return 获取 InputStream 对象, 需要执行 close 方法释放资源
     * */
    InputStream getInputStream() throws Exception;
    /**
     * @param release 是否释放资源, 为 true(释放资源)
     * @return 返回 OutputStream 数据对象, 如果参数设置为 false, 需要执行 close 方法释放资源
     * */
    OutputStream getOutputStream(boolean release) throws Exception;
    /**
     * @return 返回 OutputStream 数据对象, 需要执行 close 方法释放资源
     * */
    OutputStream getOutputStream() throws Exception;
    /**
     * @param release 是否释放资源, 为 true(释放资源)
     * @return 返回字节数组 byte[] 数据, 如果参数设置为 false, 需要执行 close 方法释放资源
     * */
    byte[] getBytes(boolean release) throws Exception;
    /**
     * @return 返回字节数组 byte[] 数据, 需要执行 close 方法释放资源
     * */
    byte[] getBytes() throws Exception;
    /**
     * 执行该 close 方法释放资源
     * */
    void close() throws Exception;
}
