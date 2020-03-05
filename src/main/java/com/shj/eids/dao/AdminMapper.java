package com.shj.eids.dao;

import com.shj.eids.domain.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName: AdminMapper
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-05 11:21
 **/
@Repository
@Mapper
public interface AdminMapper {
    public void addAdmin(Admin admin);
    public void deleteAdminById(Integer id);
    public void deleteAdminByEmail(String email);
    public void updateAdmin(Admin admin);
    public Admin getAdminById(Integer id);
    public Admin getAdminByEmail(String email);
}