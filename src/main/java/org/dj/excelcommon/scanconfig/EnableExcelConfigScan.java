package org.dj.excelcommon.scanconfig;

import org.springframework.context.annotation.Import;
import org.springframework.lang.NonNull;

import java.lang.annotation.*;

/**
 * @Author DJ
 * @Date 2024/6/12 19:56
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ScanConfigs.class)
public @interface EnableExcelConfigScan {
    @NonNull
    String[] configPackages();
}
