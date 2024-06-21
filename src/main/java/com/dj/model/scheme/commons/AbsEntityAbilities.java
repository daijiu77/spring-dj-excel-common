package com.dj.model.scheme.commons;

import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 19:01
 **/
public abstract class AbsEntityAbilities {
    private static Map<Field, Class<?>> getFieldMap(Object obj) {
        Map<Field, Class<?>> map = new HashMap<>();
        if (null == obj) return map;
        Class<?> type = obj.getClass();
        Class<?> obj_type = Object.class;
        Class<?> topEntity_type = AbsEntityAbilities.class;
        if ((type == obj_type) || (type == topEntity_type)) return map;
        if (!topEntity_type.isAssignableFrom(type)) return map;
        int num = 0;
        do {
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                map.put(field, type);
            }
            type = type.getSuperclass();
            num++;
        } while (10 > num && obj_type != type && topEntity_type != type);
        return map;
    }

    private static void setPropertyValue(Object srcObj, Object targetObj, FuncForeachSrcProperty foreachSrcProperty) {
        Map<Field, Class<?>> srcMap = getFieldMap(srcObj);
        Map<Field, Class<?>> targetMap = getFieldMap(targetObj);
        Map<String, Field> tgMap = new HashMap<>();
        for (Map.Entry<Field, Class<?>> entry : targetMap.entrySet()) {
            tgMap.put(entry.getKey().getName(), entry.getKey());
        }

        String fieldName = "";
        Object fieldValue = null;
        PropertyDescriptor pd = null;
        for (Map.Entry<Field, Class<?>> entry : srcMap.entrySet()) {
            Field srcField = entry.getKey();
            Class<?> srcType = entry.getValue();
            fieldName = srcField.getName();
            if (!tgMap.containsKey(fieldName)) continue;
            String fn = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            fieldValue = getFieldValueFromSrcObject(srcObj, srcField, srcType, fn);
            if (null != foreachSrcProperty) {
                if (!foreachSrcProperty.propertyItem(srcField.getType(), fieldName, fieldValue)) continue;
            }
            Field tgField = tgMap.get(fieldName);
            Method tgWriteMethod = getWriteMethod(targetMap, tgField, fn);
            if (null == tgWriteMethod) continue;
            if (1 != tgWriteMethod.getParameterCount()) continue;
            Class<?>[] tgClasses = tgWriteMethod.getParameterTypes();
            if (0 == tgClasses.length) continue;
            Class<?> tgFieldType = tgClasses[0];
            if (!isComplexType(tgWriteMethod, tgFieldType)) {
                if (srcField.getType() != tgFieldType) continue;
            }
            try {
                setValueToTarget(targetObj, tgWriteMethod, tgFieldType, fieldValue);
            } catch (Exception ex) {
                System.out.println("AbsEntityAbilities.setPropertyValue.01: " + ex);
            }
        }
    }

    /**
     * isComplexType 判断是复杂的数据类型
     *
     * @param method 判断参数的方法
     * @param type   参数类型
     * @return true 表示是复杂类型
     */
    private static boolean isComplexType(Method method, Class<?> type) {
        boolean isComplex = false;
        Class<?> eleType = null;
        if (type.isArray()) {
            eleType = type.getComponentType();
        } else if (List.class.isAssignableFrom(type)) {
            Type listType = method.getGenericParameterTypes()[0];
            ParameterizedType parameterizedType = (ParameterizedType) listType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            eleType = (Class<?>) actualTypeArguments[0];
        } else {
            eleType = type;
        }

        ClassLoader classLoader = eleType.getClassLoader();
        if (ClassLoader.getSystemClassLoader() == classLoader) {
            isComplex = true;
            //System.out.println("eleType - 自定义");
        } else {
            isComplex = false;
            //System.out.println("eleType - 系统的");
        }
        return isComplex;
    }

    private static Object getFieldValueFromSrcObject(Object srcObj, Field srcField, Class<?> srcType, String fn) {
        Object fieldValue = null;
        String fnRead = "get" + fn;
        Method method = null;
        try {
            method = srcType.getMethod(fnRead);
        } catch (Exception ex) {
            //continue;
        }

        if (null == method) {
            if (boolean.class == srcField.getType()) {
                fnRead = "is" + fn;
                try {
                    method = srcType.getMethod(fnRead);
                } catch (Exception ex) {
                    System.out.println("AbsEntityAbilities.getFieldValueFromSrcObject.01: " + ex);
                }
            }
        }
        if (null == method) return fieldValue;
        try {
            fieldValue = method.invoke(srcObj);
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.getFieldValueFromSrcObject.02: " + ex);
        }
        return fieldValue;
    }

    private static Method getWriteMethod(Map<Field, Class<?>> targetMap, Field tgField, String fn) {
        Class<?> targetType = targetMap.get(tgField);
        String fnWrite = "set" + fn;
        Class<?> tgFieldType = tgField.getType();
        Method method = null;
        try {
            method = targetType.getMethod(fnWrite, tgFieldType);
        } catch (Exception ex) {
            method = null;
        }

        if (null == method) {
            Method[] methods = targetType.getMethods();
            for (Method m : methods) {
                if (m.getName().equals(fnWrite)) {
                    if (1 == m.getParameterTypes().length) {
                        method = m;
                        tgFieldType = m.getParameterTypes()[0];
                        break;
                    }
                }
            }
        }
        return method;
    }

    private static void setValueToTarget(Object targetObj, Method tgMethod, Class<?> tgFieldType, Object fieldValue) {
        try {
            if (int.class == tgFieldType) {
                tgMethod.invoke(targetObj, (int) fieldValue);
            } else if (String.class == tgFieldType) {
                if (null == fieldValue) {
                    tgMethod.invoke(targetObj, null);
                } else {
                    tgMethod.invoke(targetObj, fieldValue.toString());
                }
            } else if (Date.class == tgFieldType) {
                tgMethod.invoke(targetObj, (Date) fieldValue);
            } else if (float.class == tgFieldType) {
                tgMethod.invoke(targetObj, (float) fieldValue);
            } else if (boolean.class == tgFieldType) {
                tgMethod.invoke(targetObj, (boolean) fieldValue);
            } else if (long.class == tgFieldType) {
                tgMethod.invoke(targetObj, (long) fieldValue);
            } else if (byte.class == tgFieldType) {
                tgMethod.invoke(targetObj, (byte) fieldValue);
            } else if (byte[].class == tgFieldType) {
                tgMethod.invoke(targetObj, (byte[]) fieldValue);
            } else if (double.class == tgFieldType) {
                tgMethod.invoke(targetObj, (double) fieldValue);
            } else if (short.class == tgFieldType) {
                tgMethod.invoke(targetObj, (short) fieldValue);
            } else if (char.class == tgFieldType) {
                tgMethod.invoke(targetObj, (char) fieldValue);
            } else if (tgFieldType.isArray()) {
                setArrayValue(targetObj, tgMethod, tgFieldType, fieldValue);
            } else if (List.class.isAssignableFrom(tgFieldType)) {
                setListValue(targetObj, tgMethod, tgFieldType, fieldValue);
            } else {
                setEntityValue(targetObj, tgMethod, tgFieldType, fieldValue);
            }
        } catch (Exception ex) {
            //
        }
    }

    private static <T> void setArrayValue(Object targetObj, Method tgMethod, Class<T> tgFieldType, Object fieldValue) {
        if (null == fieldValue) return;
        if (!fieldValue.getClass().isArray()) return;
        Class<?> eleType = tgFieldType.getComponentType();
        int len = Array.getLength(fieldValue);
        boolean isComplex = isComplexType(tgMethod, tgFieldType);
        Object tgArray = Array.newInstance(eleType, len);
        for (int i = 0; i < len; i++) {
            Object srcObj = Array.get(fieldValue, i);
            if (!isComplex) {
                Array.set(tgArray, i, srcObj);
                continue;
            }
            Object tgObj = null;
            try {
                tgObj = eleType.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                System.out.println("AbsEntityAbilities.setArrayValue.01: " + ex);
                break;
            }
            setPropertyValue(srcObj, tgObj, (((srcFieldType, srcPropertyName, srcPropertyValue) -> true)));
            Array.set(tgArray, i, tgObj);
        }

        try {
            tgMethod.invoke(targetObj, tgArray);
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.setArrayValue.02: " + ex);
        }
    }

    private static <T> void setListValue(Object targetObj, Method tgMethod, Class<T> tgFieldType, Object fieldValue) {
        if (null == fieldValue) return;
        if (!List.class.isAssignableFrom(fieldValue.getClass())) return;
        Type listType = tgMethod.getGenericParameterTypes()[0];
        ParameterizedType parameterizedType = (ParameterizedType) listType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Class<?> tgListEleType = (Class<?>) actualTypeArguments[0];
        List<?> srcList = (List<?>) fieldValue;
        boolean isComplex = isComplexType(tgMethod, tgFieldType);
        List<?> tgList = new ArrayList<>();
        for (Object srcObj : srcList) {
            if (!isComplex) {
                callGenericeMethod(tgList, srcObj);
                continue;
            }
            Object tgObj = null;
            try {
                tgObj = tgListEleType.getDeclaredConstructor().newInstance();
            } catch (Exception ex) {
                System.out.println("AbsEntityAbilities.setListValue.01: " + ex);
                break;
            }
            setPropertyValue(srcObj, tgObj, (((srcFieldType, srcPropertyName, srcPropertyValue) -> true)));
            callGenericeMethod(tgList, tgObj);
        }

        try {
            tgMethod.invoke(targetObj, tgList);
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.setListValue.02: " + ex);
        }
    }

    private static void callGenericeMethod(List<?> list, Object fv) {
        if (null == list) return;
        try {
            Method addMethod = List.class.getMethod("add", Object.class);
            addMethod.invoke(list, fv);
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.callGenericeMethod.01: " + ex);
        }
    }

    private static <T> void setEntityValue(Object targetObj, Method method, Class<T> tgFieldType, Object fieldValue) {
        if (null == fieldValue) return;
        Object tgObj = null;
        try {
            tgObj = tgFieldType.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.setEntityValue.01: " + ex);
            return;
        }
        setPropertyValue(fieldValue, tgObj, (((srcFieldType, srcPropertyName, srcPropertyValue) -> true)));
        try {
            method.invoke(targetObj, tgObj);
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.setEntityValue.02: " + ex);
        }
    }

    public <T> T toModel(Class<T> targetType, FuncForeachSrcProperty foreachSrcProperty) {
        Object instance = null;
        try {
            instance = targetType.getDeclaredConstructor().newInstance();
            setPropertyValue(this, instance, foreachSrcProperty);
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.toModel.01: " + ex);
            return null;
        }
        return (T) instance;
    }

    public <T> T toModel(Class<T> targetType) {
        return toModel(targetType, ((propertyType, propertyName, propertyValue) -> true));
    }

    public <T> T fromModel(Object srcModel, Class<T> targetClass, FuncForeachSrcProperty foreachSrcProperty) {
        try {
            setPropertyValue(srcModel, this, foreachSrcProperty);
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.fromModel.01: " + ex);
        }
        return (T) this;
    }

    public AbsEntityAbilities fromModel(Object srcModel, FuncForeachSrcProperty foreachSrcProperty) {
        try {
            setPropertyValue(srcModel, this, foreachSrcProperty);
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.fromModel.02: " + ex);
        }
        return this;
    }

    public <T> T fromModel(Object srcModel, Class<T> targetClass) {
        try {
            return fromModel(srcModel, targetClass, ((propertyType, propertyName, propertyValue) -> true));
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.fromModel.03: " + ex);
        }
        return (T) this;
    }

    public AbsEntityAbilities fromModel(Object srcModel) {
        try {
            return fromModel(srcModel, ((propertyType, propertyName, propertyValue) -> true));
        } catch (Exception ex) {
            System.out.println("AbsEntityAbilities.fromModel.04: " + ex);
        }
        return this;
    }

    public static <T, TT> List<T> toList(List<TT> srclist, Class<T> targetClass) {
        List<T> list = null;
        if (null == srclist) return list;
        if (srclist.isEmpty()) return list;
        list = new ArrayList<>();
        for (TT t : srclist) {
            T targetT = null;
            try {
                targetT = (T) targetClass.getDeclaredConstructor().newInstance();
                setPropertyValue(t, targetT, null);
            } catch (Exception ex) {
                System.out.println("AbsEntityAbilities.toList.01: " + ex);
                break;
            }
            list.add(targetT);
        }
        return list;
    }

    public static boolean IsBaseType(Class<?> clsType) {
        if (null == clsType) return false;
        if ((Date.class == clsType) || (String.class == clsType)) return true;
        if (!clsType.isPrimitive()) return false;
        if (void.class == clsType) return false;
        return true;
    }
}
