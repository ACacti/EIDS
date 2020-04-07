package com.shj.eids.dao;

import com.shj.eids.domain.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<Admin> getAllAdmin(@Param("offset") Integer offset, @Param("length") Integer length, @Param("levelFilter") Integer levelFilter);
    public int getCount(int levelFilter);
}