package com.dj.model.scheme.application.dto.updates;

import com.dj.model.scheme.commons.AbsEntityAbilities;

import java.io.Serializable;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 14:16
 **/
public abstract class AbsBaseUpdateDTO extends AbsEntityAbilities implements Serializable {
    protected String id;
    protected boolean is_enabled;
    protected int order_by;

    public String getId() {
        return id;
    }

    public AbsBaseUpdateDTO setId(String id) {
        this.id = id;
        return this;
    }

    public boolean isIs_enabled() {
        return is_enabled;
    }

    public AbsBaseUpdateDTO setIs_enabled(boolean is_enabled) {
        this.is_enabled = is_enabled;
        return this;
    }

    public int getOrder_by() {
        return order_by;
    }

    public AbsBaseUpdateDTO setOrder_by(int order_by) {
        this.order_by = order_by;
        return this;
    }
}
