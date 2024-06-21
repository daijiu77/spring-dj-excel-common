package org.dj.excelcommon.scanconfig;

/**
 * @Author DJ
 * @Date 2024/6/12 21:23
 **/
@FunctionalInterface
public interface FuncFile {
    boolean fileItem(Object file, FileType fileType);
}
