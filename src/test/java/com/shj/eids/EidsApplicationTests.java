package com.shj.eids;

import com.alibaba.druid.pool.DruidDataSource;
import com.shj.eids.dao.UserMapper;
import com.shj.eids.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class EidsApplicationTests {
    @Autowired
    UserMapper userMapper;
    @Test
    void mapperTest(){
        User u = userMapper.getUserById(1);
        System.out.println(u);
    }
}
