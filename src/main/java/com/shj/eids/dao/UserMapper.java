package com.shj.eids.dao;

import com.shj.eids.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: UserMapper
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 21:27
 **/
@Mapper
@Repository
public interface UserMapper {
    /*
     * @Title: getUsers
     * @Description: 此方法从数据库User表中获取符合条件的信息封装成User类返回
     * @param args: 类型为Map, key的值可以取值为user表中列名，value即为查询要求的属性，
     *              此外key的值还可以设为start和length, 表示SQL语句 limit后面的两个参数
     * @return java.util.List<com.shj.eids.domain.User>
     * @Author: ShangJin
     * @Date: 2020/3/5
     */
    public List<User> getUsers(Map<String, Object> args);
    public String getPasswordByEmail(String email);
    public void addUser(User u);
    public void updateUser(User u);
    public void deleteUserById(Integer id);
    public void deleteUserByEmail(String email);
}