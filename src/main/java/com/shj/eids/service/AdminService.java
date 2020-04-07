package com.shj.eids.service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.shj.eids.dao.*;
import com.shj.eids.domain.*;
import com.shj.eids.utils.EmailUtil;
import org.graalvm.compiler.nodes.calc.IntegerTestNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private RecordAdminManagementMapper recordAdminManagementMapper;

    @Autowired
    private RecordAdminUserMapper recordAdminUserMapper;

    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private EmailUtil emailUtil;

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

    public List<Admin> getAdmins(@Nullable Integer offset, @Nullable Integer length, int levelFilter){
        return adminMapper.getAllAdmin(offset, length, levelFilter);
    }

    public int getAdminCount(int levelFilter){
        return adminMapper.getCount(levelFilter);
    }

    public List<RecordAdminManagement> getAdminManagementRecordByManagedEmail(String email, int offset, int length){
        Admin admin = adminMapper.getAdminByEmail(email);
        if(admin == null){
            return new ArrayList<>();
        }
        return recordAdminManagementMapper.queryByManagedId(admin.getId(), offset, length);
    }

    public int getAdminManagementRecordCountByManagedEmail(String email){
        Admin admin = adminMapper.getAdminByEmail(email);
        if(admin == null){
            return 0;
        }else{
            return recordAdminManagementMapper.getCountByManagedId(admin.getId());
        }
    }

    public List<RecordAdminManagement> getAllAdminManagementRecord(int offset, int length){
        return recordAdminManagementMapper.queryAllByLimit(offset, length);
    }

    public int getAdminManagementRecordCount(){
        return recordAdminManagementMapper.getCount();
    }

    @Transactional
    public void modifyPassword(Admin admin, Integer managedId, String newPassword){
        Admin managedAdmin = adminMapper.getAdminById(managedId);
        managedAdmin.setPassword(newPassword);
        adminMapper.updateAdmin(managedAdmin);
        RecordAdminManagement record = new RecordAdminManagement();
        record.setProcessor(admin);
        record.setManagedAdmin(managedAdmin);
        record.setRecordTime(new Date());
        record.setRecordType("修改密码");
        recordAdminManagementMapper.insert(record);
    }

    @Transactional
    public void deleteAdmin(Integer []ids){
        for(Integer id: ids){
            adminMapper.deleteAdminById(id);
        }
    }

    public String sendRegisterCaptcha(String email) throws Exception {
        Admin admin = getAdminByEmail(email);
        if(admin != null){
            //检查这个邮件是否已被注册
            throw new Exception();
        }
        String captcha = defaultKaptcha.createText();
        emailUtil.sendComplexEmail("EIDS用户，您的验证码为<strong>" + captcha + "</strong>, 如果不是您的操作，请忽视本邮件。", email, "EIDS注册");
        return captcha;
    }

    @Transactional
    public void registerAdmin(String email, String password, Admin processor){
        Admin admin = new Admin(null, email, password, 1);
        adminMapper.addAdmin(admin);
        Admin managedAdmin = adminMapper.getAdminByEmail(email);
        //添加操作记录
        recordAdminManagementMapper.insert(new RecordAdminManagement(null, new Date(),managedAdmin, processor, "注册"));
    }
}