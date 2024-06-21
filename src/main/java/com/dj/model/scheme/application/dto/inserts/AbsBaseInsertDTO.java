package com.dj.model.scheme.application.dto.inserts;

import com.dj.model.scheme.commons.AbsEntityAbilities;

import java.io.Serializable;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 14:00
 **/
public abstract class AbsBaseInsertDTO extends AbsEntityAbilities implements Serializable {
    protected int order_by;
    protected boolean is_enabled;

    public boolean getIs_enabled() {
        return is_enabled;
    }

    public AbsBaseInsertDTO setIs_enabled(boolean is_enabled) {
        this.is_enabled = is_enabled;
        return this;
    }

    public int getOrder_by() {
        return order_by;
    }

    public AbsBaseInsertDTO setOrder_by(int order_by) {
        this.order_by = order_by;
        return this;
    }
}
