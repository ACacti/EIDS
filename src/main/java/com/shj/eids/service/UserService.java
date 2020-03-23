package com.shj.eids.service;

import com.shj.eids.dao.UserMapper;
import com.shj.eids.domain.User;
import com.shj.eids.exception.LoginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private UserMapper userMapper;
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
}