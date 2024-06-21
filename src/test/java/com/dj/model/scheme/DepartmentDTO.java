package com.dj.model.scheme;

import com.dj.model.scheme.application.dto.queries.AbsBaseQueryDTO;

/**
 * @Author DJ
 * @Date 2024/6/6 5:34
 **/
public class DepartmentDTO extends AbsBaseQueryDTO {
    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public DepartmentDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public DepartmentDTO setCode(String code) {
        this.code = code;
        return this;
    }
}
