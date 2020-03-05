package com.shj.eids.dao;

import com.shj.eids.domain.RecordAdminUser;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: RecordAdminUser
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-05 13:57
 **/
public interface RecordAdminUserMapper {
    public void addRecord(RecordAdminUser record);

    /*
     * @Title: getRecords
     * @Description:
     * @param args:key的值为
     *              adminId:要查询的操作记录的管理员id
     *              userId: 要查询的操作记录的用户id
     *              recordType: 要查询的操作记录的操作类型
     *              start和length：分页用的起始位置和长度
     * @return java.util.List<com.shj.eids.dao.RecordAdminUser>
     * @Author: ShangJin
     * @Date: 2020/3/5
     */
    public List<RecordAdminUser> getRecords(Map<String, Object> args);
}
