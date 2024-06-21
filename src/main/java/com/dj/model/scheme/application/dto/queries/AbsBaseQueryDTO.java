package com.dj.model.scheme.application.dto.queries;

import com.dj.model.scheme.commons.AbsEntityAbilities;
import com.dj.model.scheme.commons.DateFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 14:09
 **/
public abstract class AbsBaseQueryDTO extends AbsEntityAbilities implements Serializable {
    protected String id;
    protected boolean is_enabled;
    protected int order_by;
    protected String create_time;

    public String getId() {
        return id;
    }

    public AbsBaseQueryDTO setId(String id) {
        this.id = id;
        return this;
    }

    public boolean getIs_enabled() {
        return is_enabled;
    }

    public AbsBaseQueryDTO setIs_enabled(boolean is_enabled) {
        this.is_enabled = is_enabled;
        return this;
    }

    public int getOrder_by() {
        return order_by;
    }

    public AbsBaseQueryDTO setOrder_by(int order_by) {
        this.order_by = order_by;
        return this;
    }

    public String getCreate_time() {
        return create_time;
    }

    public AbsBaseQueryDTO setCreate_time(String create_time) {
        Date date = DateFormat.stringToDate(create_time);
        this.create_time = DateFormat.dateToString(date);
        return this;
    }
}
