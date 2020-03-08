package com.shj.eids.service;

import com.shj.eids.dao.AdminMapper;
import com.shj.eids.domain.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean loginIdentify(String email, String password){
        Admin admin = adminMapper.getAdminByEmail(email);
        if(admin != null && admin.getPassword().equals(password)){
            return true;
        }
        else {
            return false;
        }
    }
    public Admin getAdminByEmail(String email){
        return adminMapper.getAdminByEmail(email);
    }
}