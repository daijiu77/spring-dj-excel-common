package com.dj.model.scheme.table.scanner;

/**
 * @Author DJ
 * @Date 2024/6/9 19:03
 **/
public class PropertyInfo {
    private String name;
    private boolean set_name = false;
    private Class<?> type;
    private boolean set_type = false;
    private String javaType;
    private boolean set_javaType = false;
    private boolean primary = false;
    private boolean set_primary = false;
    private boolean notNull = false;
    private boolean set_notnull = false;
    private int length;
    private boolean set_length = false;

    public String getJavaType() {
        if (null == javaType) javaType = "";
        if (javaType.isEmpty()) {
            if (null != type) return type.getTypeName();
        }
        return javaType;
    }

    public PropertyInfo setJavaType(String javaType) {
        if (set_javaType) return this;
        set_javaType = true;
        this.javaType = javaType;
        return this;
    }

    public boolean getPrimary() {
        return primary;
    }

    public PropertyInfo setPrimary(boolean primary) {
        if (set_primary) return this;
        this.primary = primary;
        set_primary = true;
        return this;
    }

    public boolean getNotNull() {
        return notNull;
    }

    public PropertyInfo setNotNull(boolean notNull) {
        if (set_notnull) return this;
        this.notNull = notNull;
        set_notnull = true;
        return this;
    }

    public int getLength() {
        return length;
    }

    public PropertyInfo setLength(int length) {
        if (set_length) return this;
        this.length = length;
        set_length = true;
        return this;
    }

    public String getName() {
        return name;
    }

    public PropertyInfo setName(String name) {
        if (set_name) return this;
        this.name = name;
        set_name = true;
        return this;
    }

    public Class<?> getType() {
        return type;
    }

    public PropertyInfo setType(Class<?> type) {
        if (set_type) return this;
        this.type = type;
        set_type = true;
        return this;
    }

    @Override
    public String toString() {
        return "PropertyInfo{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
