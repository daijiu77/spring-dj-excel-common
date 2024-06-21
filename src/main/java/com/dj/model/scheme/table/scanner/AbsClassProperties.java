package com.dj.model.scheme.table.scanner;

import com.dj.model.scheme.commons.AbsEntityAbilities;
import com.dj.model.scheme.commons.EnableTableScheme;
import com.dj.model.scheme.commons.FieldMapping;
import com.dj.model.scheme.domain.models.AbsBaseModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author DJ
 * @Date 2024/6/11 4:20
 **/
public abstract class AbsClassProperties {
    protected Map<String, PropertyInfo[]> clsPropMap = null;

    protected String[] getScanModelPackages(Class<?> clsType) {
        String[] scanModelPackages = null;
        EnableTableScheme enableTableScheme = clsType.getAnnotation(EnableTableScheme.class);
        if (null != enableTableScheme) {
            scanModelPackages = enableTableScheme.scanModelPackages();
        }
        if (null == scanModelPackages) scanModelPackages = new String[0];
        return scanModelPackages;
    }

    protected void loadDataMode(Class<?> clsType, String clsName) {
        if (Modifier.isAbstract(clsType.getModifiers())) return;
        if (clsType.isInterface() || clsType.isEnum()) return;
        if (AbsBaseModel.class.isAssignableFrom(clsType) && (AbsBaseModel.class != clsType)) {
            if (clsPropMap.containsKey(clsName)) return;
            PropertyInfo[] propertyInfos = GetPropertiesFromClass(clsType);
            clsPropMap.put(clsName, propertyInfos);
        }
    }

    private PropertyInfo[] GetPropertiesFromClass(Class<?> clsType) {
        if (null == clsType) return null;
        List<PropertyInfo> list = new ArrayList<>();
        Class<?> parentType = clsType.getSuperclass();
        if ((Object.class != parentType) && (AbsEntityAbilities.class != parentType)) {
            PropertyInfo[] propertyInfos1 = GetPropertiesFromClass(parentType);
            if (null != propertyInfos1) {
                list.addAll(Arrays.asList(propertyInfos1));
            }
        }
        Field[] fields = clsType.getDeclaredFields();
        for (Field fd : fields) {
            String fName = fd.getName();
            Class<?> fType = fd.getType();
            if (!AbsEntityAbilities.IsBaseType(fType)) continue;
            FieldMapping fieldMapping = fd.getAnnotation(FieldMapping.class);
            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setType(fType);
            if (null != fieldMapping) {
                if (!fieldMapping.fieldName().isEmpty()) {
                    fName = fieldMapping.fieldName();
                }
                propertyInfo.setLength(fieldMapping.length())
                        .setPrimary(fieldMapping.isPrimaryKey())
                        .setNotNull(fieldMapping.notNull());
                String jType = fieldMapping.javaDataType();
                if (null == jType) jType = "";
                if (!jType.isEmpty()) {
                    propertyInfo.setJavaType(jType);
                }
            }
            propertyInfo.setName(fName);
            list.add(propertyInfo);
        }
        return list.toArray(new PropertyInfo[0]);
    }
}
