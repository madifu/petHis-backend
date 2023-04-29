package com.example.springboot;

import com.example.springboot.entity.Department;
import com.example.springboot.entity.User;
import com.example.springboot.mapper.DepartmentMapper;
import com.example.springboot.mapper.UserMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@MapperScan("com.example.springboot.mapper")
class SpringbootApplicationTests {

    @Autowired
    private DepartmentMapper departmentMapper;
    //private UserMapper userMapper;


    @Test
    public void testSelect() {
        System.out.println("----- selectAll method test ------");
        List<Department> userList = departmentMapper.selectList(null);
        Assert.assertEquals(0, userList.size());
        userList.forEach(System.out::println);
    }
}
