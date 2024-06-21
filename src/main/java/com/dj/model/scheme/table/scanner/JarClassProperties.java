package com.dj.model.scheme.table.scanner;

import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author DJ
 * @Date 2024/6/9 21:34
 **/
public class JarClassProperties extends AbsClassProperties {
    private String[] scanModelPackages = null;

    public void findClassesByJar(JarFile jar, Map<String, PropertyInfo[]> clsPropMap) {
        this.clsPropMap = clsPropMap;
        getEntity(jar, ((clsType, clsName) -> {
            scanModelPackages = getScanModelPackages(clsType);
            if (0 < scanModelPackages.length) return false;
            loadDataMode(clsType, clsName);
            return true;
        }));

        if (null == scanModelPackages) return;
        if (0 < scanModelPackages.length) clsPropMap.clear();
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
                JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                getEntity(jar, ((clsType, clsName) -> {
                    loadDataMode(clsType, clsName);
                    return true;
                }));
            }
        } catch (Exception e) {
            System.out.println("JarClassProperties.getEntityByPackage: " + e);
        }
    }

    private void getEntity(JarFile jar, FuncEntityClass funcEntityClass) {
        // 从此jar包 得到一个枚举类
        Enumeration<JarEntry> entry = jar.entries();
        final String clsSuffix = ".class";
        int clsSuffixLen = clsSuffix.length();
        boolean isContinue = true;
        while (entry.hasMoreElements()) {
            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文
            JarEntry jarEntry = entry.nextElement();
            String name = jarEntry.getName();
            if (jarEntry.isDirectory() || (!name.endsWith(clsSuffix))) {
                continue;
            }
            String className = name.substring(0, name.length() - clsSuffixLen);
            className = className.replace("/", ".");
            //加载类
            Class<?> clsType = loadClass(className);
            if (clsType != null) {
                className = clsType.getName();
                if (className.contains(".")) {
                    className = className.substring(className.lastIndexOf(".") + 1);
                }
                if (null != funcEntityClass) {
                    isContinue = funcEntityClass.Result(clsType, className);
                }
            }
            if (!isContinue) break;
        }
    }

    /**
     * 加载类
     *
     * @param fullClzName 类全名
     * @return
     */
    private Class<?> loadClass(String fullClzName) {
        try {
            return Class.forName(fullClzName);
        } catch (ClassNotFoundException e) {
            System.out.println("JarClassProperties.loadClass: " + e);
        }
        return null;
    }
}
