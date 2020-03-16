package com.shj.eids.service;

import com.shj.eids.dao.AdminMapper;
import com.shj.eids.dao.EpidemicMsgMapper;
import com.shj.eids.dao.UserMapper;
import com.shj.eids.domain.EpidemicMsg;
import com.shj.eids.domain.User;
import com.shj.eids.exception.UserLevelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @ClassName: EpidemicMsgService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-16 21:56
 **/
@Service
public class EpidemicMsgService {
    @Autowired
    private EpidemicMsgMapper epidemicMsgMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdminMapper adminMapper;

    public void publishArticle(@NonNull User u, @NonNull String title, String content) throws UserLevelException {
        if(u.getLevel() <= 1){
            throw new UserLevelException("没有权限");
        }
        EpidemicMsg msg = new EpidemicMsg(null, title, content, new Date(), 1, u, 0);
        epidemicMsgMapper.addEpidemicMsg(msg);
    }
}