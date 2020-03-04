package com.shj.eids.dao;

import com.shj.eids.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @ClassName: UserMapper
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 21:27
 **/
@Mapper
@Repository
public interface UserMapper {
    public User getUserById(Integer id);
}