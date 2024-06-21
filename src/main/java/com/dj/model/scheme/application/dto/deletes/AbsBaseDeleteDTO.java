package com.dj.model.scheme.application.dto.deletes;

import com.dj.model.scheme.commons.AbsEntityAbilities;

import java.io.Serializable;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 14:07
 **/
public abstract class AbsBaseDeleteDTO extends AbsEntityAbilities implements Serializable {
    protected String id;

    public String getId() {
        return id;
    }

    public AbsBaseDeleteDTO setId(String id) {
        this.id = id;
        return this;
    }
}
