package com.dj.model.scheme;

import com.dj.model.scheme.domain.models.AbsBaseModel;

import java.util.List;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/5/30 17:34
 **/
public class UserInfoModel extends AbsBaseModel {
    private String name;
    private int age;
    private float height;
    private byte num;
    private byte[] data;

    private List<DepartmentModel> departments;
    private DepartmentModel[] departmentArr;

    public DepartmentModel[] getDepartmentArr() {
        return departmentArr;
    }

    public UserInfoModel setDepartmentArr(DepartmentModel[] departmentArr) {
        this.departmentArr = departmentArr;
        return this;
    }

    public List<DepartmentModel> getDepartments() {
        return departments;
    }

    public UserInfoModel setDepartments(List<DepartmentModel> departments) {
        this.departments = departments;
        return this;
    }

    public byte getNum() {
        return num;
    }

    public UserInfoModel setNum(byte num) {
        this.num = num;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public UserInfoModel setData(byte[] data) {
        this.data = data;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserInfoModel setName(String name) {
        this.name = name;
        return this;
    }

    public int getAge() {
        return age;
    }

    public UserInfoModel setAge(int age) {
        this.age = age;
        return this;
    }

    public float getHeight() {
        return height;
    }

    public UserInfoModel setHeight(float height) {
        this.height = height;
        return this;
    }
}
