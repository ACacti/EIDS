package com.shj.eids.service;

import com.shj.eids.dao.AdminMapper;
import com.shj.eids.dao.RecordAdminAidinfoMapper;
import com.shj.eids.dao.RecordAdminUserMapper;
import com.shj.eids.dao.UserMapper;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.RecordAdminAidinfo;
import com.shj.eids.domain.RecordAdminUser;
import com.shj.eids.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: AdminService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-06 13:39
 **/
@Service
public class AdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RecordAdminUserMapper recordAdminUserMapper;

    public boolean loginIdentify(String email, String password){
        Admin admin = adminMapper.getAdminByEmail(email);
        if(admin != null && admin.getPassword().equals(password)){
            return true;
        }
        else {
            return false;
        }
    }

    public void update(Admin admin){
        adminMapper.updateAdmin(admin);
    }
    public Admin getAdminByEmail(String email){
        return adminMapper.getAdminByEmail(email);
    }

    @Transactional
    public void upgradeUser(Admin admin, User user){
        user.setLevel(user.getLevel() + 1);
        userMapper.updateUser(user);
        RecordAdminUser record = new RecordAdminUser(null, admin, user, new Date(), "提升权限");
        recordAdminUserMapper.addRecord(record);
    }

    @Transactional
    public void downgradeUser(Admin admin, User user){
        user.setLevel(user.getLevel() - 1);
        userMapper.updateUser(user);
        RecordAdminUser record = new RecordAdminUser(null, admin, user, new Date(), "降低权限");
        recordAdminUserMapper.addRecord(record);
    }

    public List<RecordAdminUser> getRecords(Admin admin, @Nullable User user, @Nullable Integer start, @Nullable Integer length){
        Map<String, Object> args = new HashMap<>();
        args.put("adminId", admin.getId());
        if(user !=null){
            args.put("userId", user.getId());
        }
        args.put("start", start);
        args.put("length", length);
        return recordAdminUserMapper.getRecords(args);
    }

    public Integer getAdminUserRecordCount(Admin admin, @Nullable User user){
        Map<String, Object> args = new HashMap<>();
        args.put("adminId", admin.getId());
        if(user !=null) {
            args.put("userId", user.getId());
        }
        return recordAdminUserMapper.getCount(args);
    }

}