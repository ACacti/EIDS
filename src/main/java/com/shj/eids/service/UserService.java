package com.shj.eids.service;

import com.shj.eids.dao.EpidemicMsgMapper;
import com.shj.eids.dao.RecordAdminUserMapper;
import com.shj.eids.dao.UserMapper;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.EpidemicMsg;
import com.shj.eids.domain.RecordAdminUser;
import com.shj.eids.domain.User;
import com.shj.eids.exception.LoginException;
import com.shj.eids.utils.EmailUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: UserService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 09:58
 **/
@Service
public class UserService {
    @Autowired
    RecordAdminUserMapper recordAdminUserMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private EpidemicMsgMapper epidemicMsgMapper;

    public UserService(){}
    @Transactional
    public void userRegister(User u) throws LoginException {
        String password = userMapper.getPasswordByEmail(u.getEmail());
        if(password != null){
            throw new LoginException("注册邮箱已存在");
        }
        else {
            try{
                userMapper.addUser(u);
            }catch (Exception e){
                throw new LoginException("账号信息错误");
            }
        }
    }

    @Transactional
    public void updateUser(User u){
        userMapper.updateUser(u);
    }
    public boolean loginIdentify(String email, String password){
        String rawPassword = userMapper.getPasswordByEmail(email);
        if(rawPassword!=null && rawPassword.equals(password)){
            return true;
        }
        else {
            return false;
        }
    }

    public List<User> getUsers(@Nullable Integer id, @Nullable String email, @Nullable String password,
                               @Nullable Integer level, @Nullable Boolean fuzzy, @Nullable Integer start,
                               @Nullable Integer length){
        Map<String, Object> args = new HashMap<>();
        args.put("id", id);
        args.put("email", email);
        args.put("password", password);
        args.put("level", level);
        args.put("fuzzy", fuzzy);
        args.put("start", start);
        args.put("length", length);
        return userMapper.getUsers(args);
    }
    public List<User> getUsers(Map<String, Object> args){
        return userMapper.getUsers(args);
    }

    public User getUserByEmail(String email){
        HashMap<String, Object> map = new HashMap<>();
        map.put("email", email);
        List<User> list = userMapper.getUsers(map);
        if(list.size() >= 1){
            return list.get(0);
        }
        return null;
    }

    public User getUserById(Integer id){
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        return userMapper.getUsers(map).get(0);
    }

    public boolean hasEmail(String email){
        String passwd = userMapper.getPasswordByEmail(email);
        if(passwd == null){
            return false;
        }else {
            return true;
        }
    }

    public Integer getCount(Map<String, Object> args){
        return userMapper.getCount(args);
    }

    public Integer getCount(@Nullable Integer id, @Nullable String email, @Nullable String password,
                            @Nullable Integer level, @Nullable Boolean fuzzy, @Nullable Integer start,
                            @Nullable Integer length){
        Map<String, Object> args = new HashMap<>();
        args.put("id", id);
        args.put("email", email);
        args.put("password", password);
        args.put("level", level);
        args.put("fuzzy", fuzzy);
        args.put("start", start);
        args.put("length", length);
        return userMapper.getCount(args);
    }

    @Transactional
    public void downgradeUser(Admin admin, User user) throws MessagingException {
        user.setLevel(user.getLevel() - 1);
        user.setIntroduction("");
        userMapper.updateUser(user);
        //生成操作记录
        RecordAdminUser record = new RecordAdminUser(null, admin, user, new Date(), "降低权限");
        recordAdminUserMapper.addRecord(record);
        //被降级为普通用户的作者会删除其发布的文章
        if(user.getLevel() <= 1){
            HashMap<String, Object> args = new HashMap<>();
            args.put("authorId", user.getId());
            epidemicMsgMapper.deleteEpidemicMsg(args);
        }
        //邮件通知
        emailUtil.sendComplexEmailByAsynchronousMode("EIDS用户，您已被管理员降级，如有疑问请联系管理员" + admin.getEmail(), user.getEmail(), "权限降级");
    }

    @Transactional
    public void upgradeUser(Admin admin, User user, String introduction) throws MessagingException {
        user.setLevel(user.getLevel() + 1);
        user.setIntroduction(introduction);
        userMapper.updateUser(user);
        //生成操作记录
        RecordAdminUser record = new RecordAdminUser(null, admin, user, new Date(), "提升权限");
        recordAdminUserMapper.addRecord(record);
        //邮件通知
        emailUtil.sendComplexEmailByAsynchronousMode("EIDS用户，您被管理员升级，认证名为:<em>" + introduction + "</em>，详情联系管理员<em>" + admin.getEmail() + "</em>", user.getEmail(), "权限升级") ;
    }



}