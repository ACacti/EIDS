package com.shj.eids;

import com.alibaba.druid.pool.DruidDataSource;
import com.shj.eids.dao.BrowseHistoryMapper;
import com.shj.eids.dao.UserMapper;
import com.shj.eids.domain.BrowseHistory;
import com.shj.eids.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
class EidsApplicationTests {
    @Autowired
    BrowseHistoryMapper mapper;
    @Autowired
    UserMapper userMapper;
    @Test
    void contextLoad(){
        BrowseHistory browseHistory = new BrowseHistory();
        User user = new User();
        browseHistory.setBrowseTime(new Date());
        Map<String, Object> map = new HashMap<>();
        map.put("email", "shangjinv6@163.com");
        user = userMapper.getUsers(map).get(0);
        browseHistory.setUser(user);
        mapper.addHistory(browseHistory);
    }

}
