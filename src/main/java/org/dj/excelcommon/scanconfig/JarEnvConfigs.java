package org.dj.excelcommon.scanconfig;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @Author DJ
 * @Date 2024/6/12 20:36
 **/
public class JarEnvConfigs extends AbsAnalysisConfig {
    private String[] mappingConfigs = null;

    public void Execute(URL url) throws Exception {
        JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
        getFile(jar, FileType.cls, ((file, fileType) -> {
            if (null == file) return true;
            Class<?> clsType = (Class<?>) file;
            mappingConfigs = getMappingConfigs(clsType);
            return null == mappingConfigs;
        }));

        if (null == mappingConfigs) return;
        for (String pack : mappingConfigs) {
            MappingConfigItem(pack);
        }
    }

    private void MappingConfigItem(String pack) {
        String packDir = pack.replace(".", "/");
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packDir);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                getFile(jar, FileType.xml, ((file, fileType) -> {
                    if (null == file) return true;
                    JarEntry jarEntry = (JarEntry) file;
                    String fPath = jarEntry.getName();
                    ClassLoader classLoader = getClass().getClassLoader();
                    InputStream inputStream = null;
                    try {
                        inputStream = classLoader.getResourceAsStream(fPath);
                        analysisXML(inputStream);
                    } catch (Exception e) {
                        System.out.println("+++++++++++++  information - start +++++++++++++++++++++++");
                        System.out.println("JarEnvConfigs.MappingConfigItem - 01: " + e);
                        System.out.println("-------------  information - end   -----------------------");
                    } finally {
                        if (null != inputStream) {
                            try {
                                inputStream.close();
                            } catch (Exception e) {
                                //
                            }
                        }
                    }
                    return true;
                }));
            }
        } catch (Exception e) {
            System.out.println("+++++++++++++  information - start +++++++++++++++++++++++");
            System.out.println("JarEnvConfigs.MappingConfigItem - 02: " + e);
            System.out.println("-------------  information - end   -----------------------");
        }
    }

    private void getFile(JarFile jar, FileType fileType, FuncFile funcFile) {
        Enumeration<JarEntry> entry = jar.entries();
        final String clsExt = ".class";
        final String xmlExt = ".xml";
        int clsLen = clsExt.length();
        int xmlLen = xmlExt.length();
        while (entry.hasMoreElements()) {
            JarEntry jarEntry = entry.nextElement();
            if (jarEntry.isDirectory()) continue;
            String name = jarEntry.getName();
            if (FileType.cls == fileType) {
                int len = name.length() - clsLen;
                if (!name.substring(len).equals(clsExt)) continue;
                if (null == funcFile) continue;
                String clsName = name.substring(0, len);
                clsName = clsName.replace("/", ".");
                Class<?> clsType = null;
                try {
                    clsType = Class.forName(clsName);
                } catch (Exception e) {
                    System.out.println("+++++++++++++  information - start +++++++++++++++++++++++");
                    System.out.println("JarEnvConfigs.getFile: " + e);
                    System.out.println("JarEnvConfigs.getFile: " + clsName);
                    System.out.println("-------------  information - end   -----------------------");
                }
                if (!funcFile.fileItem(clsType, fileType)) break;
            } else {
                if (!name.substring(name.length() - xmlLen).equals(xmlExt)) continue;
                if (null == funcFile) continue;
                if (!funcFile.fileItem(jarEntry, fileType)) break;
            }
        }
    }
}
