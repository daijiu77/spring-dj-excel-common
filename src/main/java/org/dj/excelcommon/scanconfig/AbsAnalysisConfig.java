package org.dj.excelcommon.scanconfig;

import org.dj.excelcommon.excel.AbsExcelService;
import org.dj.excelcommon.scanconfig.entities.ColumnInfo;
import org.dj.excelcommon.scanconfig.entities.TableInfo;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author DJ
 * @Date 2024/6/12 20:37
 **/
public abstract class AbsAnalysisConfig {
    /**
     * tableInfoMap - key: 小写的表名,对应配置文件里 FieldMappings.table 属性值, value: 配置文件的完整信息
     */
    public static Map<String, TableInfo> tableInfoMap = new HashMap<>();

    /**
     * tableMethodMap - key: 方法的名称(小写), value: Method 对象
     */
    private Map<String, Method> tableMethodMap = new HashMap<>();

    /**
     * columnMethodMap - key: 方法的名称(小写), value: Method 对象
     */
    private Map<String, Method> columnMethodMap = new HashMap<>();

    public AbsAnalysisConfig() {
        setMethodMap(TableInfo.class, tableMethodMap);
        setMethodMap(ColumnInfo.class, columnMethodMap);
    }

    protected String[] getMappingConfigs(Class<?> clsType) {
        if (null == clsType) return null;
        EnableExcelConfigScan enableExcelConfigScan = clsType.getAnnotation(EnableExcelConfigScan.class);
        if (null == enableExcelConfigScan) return null;
        return enableExcelConfigScan.configPackages();
    }

    protected void analysisXML(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newDefaultInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(inputStream);
        doc.getDocumentElement().normalize();
        NodeList nodes = doc.getChildNodes();

        final String FieldMappings = "fieldmappings";
        int len = nodes.getLength();
        for (int i = 0; i < len; i++) {
            Node node = nodes.item(i);
            if (Node.ELEMENT_NODE != node.getNodeType()) continue;
            Element element = (Element) node;
            String tagNameLower = element.getTagName().toLowerCase();
            if (!tagNameLower.equals(FieldMappings)) continue;
            //Get table info
            TableInfo tableInfo = new TableInfo();
            setEntityPropValueByNodeAttr(node, tableInfo, tableMethodMap);
            String title = tableInfo.getTitle();
            if (!title.isEmpty()) {
                tableInfo.addDataRowIndex();
            }
            //Get column info
            NodeList childs = node.getChildNodes();
            List<ColumnInfo> list = new ArrayList<>();
            boolean exitChilds = false;
            int childLen = childs.getLength();
            if (0 < childLen) tableInfo.addDataRowIndex();
            for (int ii = 0; ii < childLen; ii++) {
                Node childNode = childs.item(ii);
                if (Node.ELEMENT_NODE != childNode.getNodeType()) continue;
                ColumnInfo columnInfo = new ColumnInfo();
                setEntityPropValueByNodeAttr(childNode, columnInfo, columnMethodMap);
                if (getChilds(childNode, columnInfo, 1, 0)) {
                    exitChilds = true;
                }
                columnInfo.setTableInfo(tableInfo);
                list.add(columnInfo);
            }
            if (exitChilds) tableInfo.addDataRowIndex();
            //Asc 按属性 index 由小到大排序
            list.sort(new Comparator<ColumnInfo>() {
                @Override
                public int compare(ColumnInfo o1, ColumnInfo o2) {
                    return o1.getIndex() - o2.getIndex();
                }
            });
            tableInfo.setColumnInfos(list);
            String tbName = tableInfo.getTable().toLowerCase();
            tableInfoMap.remove(tbName);
            tableInfoMap.put(tbName, tableInfo);
        }
    }

    private void setMethodMap(Class<?> clsType, Map<String, Method> methodMap) {
        Field[] fields = clsType.getDeclaredFields();
        for (Field fd : fields) {
            String fName = fd.getName();
            String fn = "set" + fName.substring(0, 1).toUpperCase() + fName.substring(1);
            Method method = null;
            try {
                method = clsType.getMethod(fn, fd.getType());
            } catch (Exception e) {
                //
            }
            if (null == method) {
                Method[] methods = clsType.getMethods();
                fn = fName.toLowerCase();
                for (Method md : methods) {
                    String mnLower = md.getName().toLowerCase();
                    if (mnLower.startsWith("set") && mnLower.contains(fn)) {
                        method = md;
                        break;
                    }
                }
            }
            if (null == method) continue;
            methodMap.put(fName.toLowerCase(), method);
        }
    }

    private boolean getChilds(Node node, ColumnInfo columnInfo, int maxLevel, int currentLevel) {
        if (maxLevel <= currentLevel) return false;
        currentLevel++;
        NodeList nodes = node.getChildNodes();
        int len = nodes.getLength();
        List<ColumnInfo> columnInfos = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            Node itemNode = nodes.item(i);
            if (Node.ELEMENT_NODE != itemNode.getNodeType()) continue;
            ColumnInfo column = new ColumnInfo();
            setEntityPropValueByNodeAttr(itemNode, column, columnMethodMap);
            getChilds(itemNode, column, maxLevel, currentLevel);
            columnInfos.add(column);
        }
        columnInfo.setChildren(columnInfos);
        return !columnInfos.isEmpty();
    }

    private void setEntityPropValueByNodeAttr(Node node, Object obj, Map<String, Method> methodMap) {
        if (null == node) return;
        NamedNodeMap map = node.getAttributes();
        int len = map.getLength();
        for (int i = 0; i < len; i++) {
            Node attrNode = map.item(i);
            String key = attrNode.getNodeName().toLowerCase();
            String val = attrNode.getNodeValue();
            if (!methodMap.containsKey(key)) continue;
            Method method = methodMap.get(key);
            try {
                Object v = convertTo(method, val);
                method.invoke(obj, v);
            } catch (Exception e) {
                System.out.println("AbsAnalysisConfig.setEntityPropValueByNodeAttr: " + e);
            }
        }
    }

    private Object convertTo(Method method, String val) {
        Parameter[] paras = method.getParameters();
        if (0 == paras.length) return val;
        Class<?> type = paras[0].getType();
        return convertValueByType(type, val);
    }

    public static Object convertValueByType(Class<?> type, String val) {
        if (String.class == type) {
            return val;
        } else if (int.class == type) {
            if (val.contains(".")) {
                val = val.substring(0, val.indexOf("."));
            }
            return Integer.parseInt(val);
        } else if (boolean.class == type) {
            val = val.toLowerCase();
            if (!val.equalsIgnoreCase("true")) val = "false";
            return Boolean.parseBoolean(val);
        } else if (float.class == type) {
            return Float.parseFloat(val);
        } else if (double.class == type) {
            return Double.parseDouble(val);
        } else if (Date.class == type) {
            return stringToDate(val);
        } else if (Calendar.class == type) {
            Date date = stringToDate(val);
            if (null == date) return null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        return null;
    }

    public static Date stringToDate(String dateStr) {
        Date date = new Date();
        boolean success = false;
        try {
            date = new Date(dateStr);
            success = true;
        } catch (Exception ex) {
            //System.out.println("DateFormat.stringToDate.01: "+ex);
        }

        if (!success) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = dateFormat.parse(dateStr);
            } catch (Exception ex) {
                System.out.println("DateFormat.stringToDate.02: " + ex);
            }
        }
        return date;
    }
}
