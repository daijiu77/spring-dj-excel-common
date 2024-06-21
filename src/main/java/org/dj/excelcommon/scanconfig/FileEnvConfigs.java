package org.dj.excelcommon.scanconfig;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author DJ
 * @Date 2024/6/12 22:50
 **/
public class FileEnvConfigs extends AbsAnalysisConfig {
    private String[] mappingConfigs = null;

    public void Execute(String rootPath) {
        getFile(rootPath, FileType.cls, null, ((file, fileType) -> {
            if (null == file) return true;
            Class<?> clsType = (Class<?>) file;
            mappingConfigs = getMappingConfigs(clsType);
            return null == mappingConfigs;
        }));
        if (null == mappingConfigs) return;
        for (String pack : mappingConfigs) {
            getFileByPackage(pack);
        }
    }

    private void getFileByPackage(String pack) {
        String packDir = pack.replace(".", "/");
        try {
            Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packDir);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("jar".equals(protocol)) {
                    JarEnvConfigs jarEnvConfigs = new JarEnvConfigs();
                    try {
                        jarEnvConfigs.Execute(url);
                    } catch (Exception e) {
                        System.out.println("+++++++++++++  information - start +++++++++++++++++++++++");
                        System.out.println("FileEnvConfigs.getFileByPackage - 01: " + e);
                        System.out.println("-------------  information - end   -----------------------");
                    }
                } else {
                    String rootPath = URLDecoder.decode(url.getFile(), "UTF-8");
                    getFile(rootPath, FileType.xml, pack, ((file, fileType) -> {
                        if (null == file) return true;
                        String fPath = file.toString();
                        ClassLoader classLoader = getClass().getClassLoader();
                        InputStream inputStream = null;
                        try {
                            inputStream = classLoader.getResourceAsStream(fPath);
                            analysisXML(inputStream);
                        } catch (Exception e) {
                            System.out.println("+++++++++++++  information - start +++++++++++++++++++++++");
                            System.out.println("FileEnvConfigs.getFileByPackage - 02: " + e);
                            System.out.println("-------------  information - end   -----------------------");
                        } finally {
                            if (null != inputStream) {
                                try {
                                    inputStream.close();
                                } catch (Exception e) {
                                    System.out.println("+++++++++++++  information - start +++++++++++++++++++++++");
                                    System.out.println("FileEnvConfigs.getFileByPackage - 03: " + e);
                                    System.out.println("-------------  information - end   -----------------------");
                                }
                            }
                        }
                        return true;
                    }));
                }
            }
        } catch (Exception e) {
            System.out.println("FileClassProperties.getEntityByPackage - 02: " + e);
        }
    }

    private void getFile(String rootPath, FileType fileType, String packagePath, FuncFile funcFile) {
        File file = new File(rootPath);
        if (!file.exists()) return;
        if (!file.isDirectory()) return;
        String finalPackagePath = packagePath;
        final boolean[] isContinue = {true};
        File[] files = file.listFiles(new FilenameFilter() {
            private Pattern patternCls = Pattern.compile("([a-zA-Z0-9_]+)\\.class");
            private final String xmlExt = ".xml";

            @Override
            public boolean accept(File dir, String name) {
                if (!isContinue[0]) return false;
                if (FileType.cls == fileType) {
                    Matcher matcher = patternCls.matcher(name);
                    if (!matcher.find()) return true;
                    String clsName = matcher.group(1);
                    String pgPath = finalPackagePath;
                    if (null == pgPath) pgPath = "";
                    String clsPath = pgPath + "." + clsName;
                    if (pgPath.isEmpty()) {
                        clsPath = dir.getPath().replace("/", ".");
                        clsPath += "." + clsName;
                    }
                    try {
                        Class<?> clsType = Class.forName(clsPath);
                        if (null != funcFile) {
                            isContinue[0] = funcFile.fileItem(clsType, fileType);
                        }
                    } catch (Exception e) {
                        //System.out.println(e);
                    }
                } else if (FileType.xml == fileType) {
                    if (!name.toLowerCase().substring(name.length() - xmlExt.length()).equals(xmlExt)) return true;
                    if (null != funcFile) {
                        String fPath = finalPackagePath.replace(".","/") + "/" + name;
                        isContinue[0] = funcFile.fileItem(fPath, fileType);
                    }
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
            getFile(dir.getPath(), fileType, currPackage, funcFile);
        }
    }
}
