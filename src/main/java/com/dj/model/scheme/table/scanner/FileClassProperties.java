package com.dj.model.scheme.table.scanner;

import java.io.File;
import java.io.FilenameFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author DJ
 * @Date 2024/6/11 3:23
 **/
public class FileClassProperties extends AbsClassProperties {
    private String[] scanModelPackages = null;

    public void findClassByFile(String rootPath, Map<String, PropertyInfo[]> clsPropMap) {
        this.clsPropMap = clsPropMap;
        getEntities(rootPath, "", (clsType, clsName) -> {
            scanModelPackages = getScanModelPackages(clsType);
            if (0 < scanModelPackages.length) return false;

            loadDataMode(clsType, clsName);
            return true;
        });

        if (null == scanModelPackages) return;
        for (String pg : scanModelPackages) {
            getEntityByPackage(pg);
        }
    }

    private void getEntityByPackage(String pack) {
        String packDir = pack.replace(".", "/");
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packDir);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("jar".equals(protocol)) {
                    JarClassProperties jarClassProperties = new JarClassProperties();
                    try {
                        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        jarClassProperties.findClassesByJar(jar, clsPropMap);
                    } catch (Exception e) {
                        System.out.println("FileClassProperties.getEntityByPackage - 01: " + e);
                    }
                } else {
                    String rootPath = URLDecoder.decode(url.getFile(), "UTF-8");
                    getEntities(rootPath, pack, (clsType, clsName) -> {
                        loadDataMode(clsType, clsName);
                        return true;
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("FileClassProperties.getEntityByPackage - 02: " + e);
        }
    }

    private void getEntities(String rootPath, String packagePath, FuncEntityClass entityClass) {
        File file = new File(rootPath);
        if (!file.exists()) return;
        if (!file.isDirectory()) return;
        String finalPackagePath = packagePath;
        final boolean[] isContinue = {true};
        File[] files = file.listFiles(new FilenameFilter() {
            private Pattern pattern = Pattern.compile("([a-zA-Z0-9_]+)\\.class");

            @Override
            public boolean accept(File dir, String name) {
                if (!isContinue[0]) return false;
                if (name.matches(".+\\.class$")) {
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.find()) {
                        String clsName = matcher.group(1);
                        String clsPath = finalPackagePath + "." + clsName;
                        try {
                            Class<?> clsType = Class.forName(clsPath);
                            if (null != entityClass) {
                                isContinue[0] = entityClass.Result(clsType, clsName);
                            }
                        } catch (Exception e) {
                            //System.out.println(e);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        if (!isContinue[0]) return;

        File[] dirs = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.matches("^([a-zA-Z0-9_]+)$")) {
                    return true;
                }
                return false;
            }
        });

        if (null == packagePath) packagePath = "";
        for (File dir : dirs) {
            String currPackage = packagePath;
            if (currPackage.isEmpty()) {
                currPackage = dir.getName();
            } else {
                currPackage += "." + dir.getName();
            }
            getEntities(dir.getPath(), currPackage, entityClass);
        }
    }
}
