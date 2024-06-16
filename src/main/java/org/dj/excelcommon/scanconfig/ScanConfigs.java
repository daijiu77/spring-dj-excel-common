package org.dj.excelcommon.scanconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Objects;

/**
 * @Author DJ
 * @Date 2024/6/12 20:12
 **/
@Component
@ComponentScan(basePackages = {"org.dj.excelcommon.excel.exportdata", "org.dj.excelcommon.excel.importdata"})
public class ScanConfigs {
    @Bean
    public void scan() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL currUrl = classLoader.getResource("");
        String protocol = "file";
        if (null != currUrl) {
            protocol = currUrl.getProtocol();
        }
        if (protocol.equals("jar")) {
            JarEnvConfigs jarEnvConfigs = new JarEnvConfigs();
            try {
                jarEnvConfigs.Execute(currUrl);
            } catch (Exception e) {
                System.out.println("ScanMapping.scan: " + e);
            }
        } else {
            String rootPath = Objects.requireNonNull(currUrl).getPath();
            FileEnvConfigs fileEnvConfigs = new FileEnvConfigs();
            fileEnvConfigs.Execute(rootPath);
        }
    }
}
