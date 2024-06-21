package com.dj.model.scheme;

import com.dj.model.scheme.domain.models.AbsBaseModel;

/**
 * @Author DJ
 * @Date 2024/6/6 5:32
 **/
public class DepartmentModel extends AbsBaseModel {
    private String name;
    private String code;

    public String getCode() {
        return code;
    }

    public DepartmentModel setCode(String code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public DepartmentModel setName(String name) {
        this.name = name;
        return this;
    }
}
