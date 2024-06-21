package com.dj.model.scheme.domain.models;

import com.dj.model.scheme.commons.AbsEntityAbilities;
import com.dj.model.scheme.commons.DateFormat;
import com.dj.model.scheme.commons.FieldMapping;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 13:57
 **/
public abstract class AbsBaseModel extends AbsEntityAbilities implements Serializable {
    @FieldMapping(javaDataType = "UUID", length = 36, isPrimaryKey = true)
    protected String id;
    protected boolean is_enabled;
    protected int order_by;
    @FieldMapping(length = 30, javaDataType = "Date")
    protected String create_time;

    public String getId() {
        if (null == id) {
            id = UUID.randomUUID().toString();
        }
        return id;
    }

    public AbsBaseModel setId(String id) {
        this.id = id;
        return this;
    }

    public boolean getIs_enabled() {
        return is_enabled;
    }

    public AbsBaseModel setIs_enabled(boolean is_enabled) {
        this.is_enabled = is_enabled;
        return this;
    }

    public int getOrder_by() {
        return order_by;
    }

    public AbsBaseModel setOrder_by(int order_by) {
        this.order_by = order_by;
        return this;
    }

    public String getCreate_time() {
        if (null == create_time) {
            Date date = new Date();
            create_time = DateFormat.dateToString(date);
        }
        return create_time;
    }

    public AbsBaseModel setCreate_time(String create_time) {
        this.create_time = create_time;
        return this;
    }
}
