package com.dj.model.scheme;

import com.dj.model.scheme.commons.AbsEntityAbilities;
import com.dj.model.scheme.commons.DateFormat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class SpringDjModelSchemeApplicationTests {

    @Test
    void contextLoads() {
        UserInfoModel userInfo = new UserInfoModel();
        userInfo.setName("DJ").setAge(22).setHeight(1.7f).setNum((byte) 105).setData(new byte[]{(byte) 11, (byte) 15});
        userInfo.setIs_enabled(true).setCreate_time(DateFormat.dateToString(new Date())).setOrder_by(5);

        //List 测试
        List<DepartmentModel> list1 = new ArrayList<>();
        DepartmentModel depart = new DepartmentModel();
        depart.setCode("abc").setName("Develop");
        list1.add(depart);

        depart = new DepartmentModel();
        depart.setCode("abc1").setName("Develop1");
        list1.add(depart);
        userInfo.setDepartments(list1);

        //Array 测试
        DepartmentModel[] departArr = new DepartmentModel[2];
        depart = new DepartmentModel();
        depart.setCode("abc-1").setName("Develop-1");
        departArr[0] = depart;

        depart = new DepartmentModel();
        depart.setCode("abc-2").setName("Develop-2");
        departArr[1] = depart;
        userInfo.setDepartmentArr(departArr);

        UserInfoDTO dto = null;
        try {
            dto = userInfo.toModel(UserInfoDTO.class);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        dto = new UserInfoDTO();
        dto.fromModel(userInfo, UserInfoDTO.class);
        List<UserInfoModel> list = new ArrayList<>();
        list.add(userInfo);
        List<UserInfoDTO> dtos = AbsEntityAbilities.toList(list, UserInfoDTO.class);
        if (null == dtos) return;
        int len = dtos.size();
    }

    @Test
    void Test1() {
        String clsPath = "E:\\Projects\\jee\\idea\\demo-nacos\\base-commons\\target\\test-classes\\org\\example\\basecommons\\Product.class";
        Class cls = null;
        Object product = null;
        try {
            cls = Class.forName("org.example.basecommons.Product");
            product = cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.out.println("+++++++++++++++++++++++ Error ++++++++++++++++++++++++++");
            System.out.println(e);
        }
    }
}
