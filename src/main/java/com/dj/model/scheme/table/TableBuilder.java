package com.dj.model.scheme.table;

import com.dj.model.scheme.table.builder.MSSqlServerBuilder;
import com.dj.model.scheme.table.builder.MySqlBuilder;
import com.dj.model.scheme.table.builder.OracleBuilder;
import com.dj.model.scheme.table.scanner.FileClassProperties;
import com.dj.model.scheme.table.scanner.JarClassProperties;
import com.dj.model.scheme.table.scanner.PropertyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.JarURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author DJ
 * @Date 2024/6/8 3:03
 **/
@Configuration
public class TableBuilder {
    @Autowired
    private Environment environment;

    @Bean
    public void init() {
        if (null != environment) {
            String url = environment.getProperty("spring.datasource.url");
            String username = environment.getProperty("spring.datasource.username");
            String password = environment.getProperty("spring.datasource.password");
            if (null != url) {
                System.out.println("######################### table scheme - start #######################################");
                Map<String, PropertyInfo[]> clsPropMap = GetDataModels();
                url = url.trim();
                Pattern pattern = Pattern.compile("^jdbc:([a-zA-Z]+):");
                Matcher matcher = pattern.matcher(url);
                if (matcher.find()) {
                    String dbType = matcher.group(1);
                    dbType = dbType.toLowerCase();
                    if (dbType.equals("mysql")) {
                        MySqlBuilder sqlBuilder = new MySqlBuilder(url, username, password);
                        sqlBuilder.Exec(clsPropMap);
                    } else if (dbType.equals("sqlserver")) {
                        MSSqlServerBuilder msSqlServerBuilder = new MSSqlServerBuilder(url, username, password);
                        msSqlServerBuilder.Exec(clsPropMap);
                    } else if (dbType.equals("oracle")) {
                        OracleBuilder oracleBuilder = new OracleBuilder(url, username, password);
                        oracleBuilder.Exec(clsPropMap);
                    }
                }
                System.out.println("######################### table scheme - end   #######################################");
            }
        }
    }

    /**
     * @return - key 为类名的原字母(区分大小写), value 为属性信息 PropertyInfo 的集合
     */
    private Map<String, PropertyInfo[]> GetDataModels() {
        Map<String, PropertyInfo[]> clsPropMap = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        URL currUrl = classLoader.getResource("");
        String protocol = "file";
        if (null != currUrl) {
            protocol = currUrl.getProtocol();
        }
        assert currUrl != null;
        System.out.println("url: " + currUrl.getPath());
        System.out.println("Protocol: " + protocol);
        if ("jar".equals(protocol)) {
            JarClassProperties jarClassProperties = new JarClassProperties();
            try {
                JarFile jar = ((JarURLConnection) currUrl.openConnection()).getJarFile();
                jarClassProperties.findClassesByJar(jar, clsPropMap);
            } catch (Exception e) {
                System.out.println("PhysicsTable.GetDataModels - 01: " + e);
            }
        } else {
            String rootPath = Objects.requireNonNull(currUrl).getPath();
            FileClassProperties fileClassProperties = new FileClassProperties();
            try {
                fileClassProperties.findClassByFile(rootPath, clsPropMap);
            } catch (Exception e) {
                System.out.println("PhysicsTable.GetDataModels - 02: " + e);
            }
        }
        System.out.println("DataModelMap size: " + clsPropMap.size());
        return clsPropMap;
    }
}
