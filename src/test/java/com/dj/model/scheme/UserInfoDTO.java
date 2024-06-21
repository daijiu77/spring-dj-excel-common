package com.dj.model.scheme;

import com.dj.model.scheme.application.dto.queries.AbsBaseQueryDTO;

import java.util.List;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 17:33
 **/
public class UserInfoDTO extends AbsBaseQueryDTO {
    private String name;
    private int age;
    private float height;
    private byte num;
    private byte[] data;
    private List<DepartmentDTO> departments;
    private DepartmentDTO[] departmentArr;

    public DepartmentDTO[] getDepartmentArr() {
        return departmentArr;
    }

    public UserInfoDTO setDepartmentArr(DepartmentDTO[] departmentArr) {
        this.departmentArr = departmentArr;
        return this;
    }

    public List<DepartmentDTO> getDepartments() {
        return departments;
    }

    public UserInfoDTO setDepartments(List<DepartmentDTO> departments) {
        this.departments = departments;
        return this;
    }

    public byte getNum() {
        return num;
    }

    public UserInfoDTO setNum(byte num) {
        this.num = num;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public UserInfoDTO setData(byte[] data) {
        this.data = data;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserInfoDTO setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public UserInfoDTO setAge(int age) {
        this.age = age;
        return this;
    }

    public float getHeight() {
        return height;
    }

    public UserInfoDTO setHeight(float height) {
        this.height = height;
        return this;
    }
}
