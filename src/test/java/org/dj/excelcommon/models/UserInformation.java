package org.dj.excelcommon.models;

/**
 * @Author DJ
 * @Description New class
 * @CreateDate 2024/6/13 15:28
 **/
public class UserInformation {
    private String name;
    private String sex;
    private int age;
    private String phone;
    private float chinese;
    private float physics;
    private String address;

    public String getName() {
        return name;
    }

    public UserInformation setName(String name) {
        this.name = name;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public UserInformation setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public int getAge() {
        return age;
    }

    public UserInformation setAge(int age) {
        this.age = age;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserInformation setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public float getChinese() {
        return chinese;
    }

    public UserInformation setChinese(float chinese) {
        this.chinese = chinese;
        return this;
    }

    public float getPhysics() {
        return physics;
    }

    public UserInformation setPhysics(float physics) {
        this.physics = physics;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserInformation setAddress(String address) {
        this.address = address;
        return this;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", phone='" + phone + '\'' +
                ", chinese=" + chinese +
                ", physics=" + physics +
                ", address='" + address + '\'' +
                '}';
    }
}
