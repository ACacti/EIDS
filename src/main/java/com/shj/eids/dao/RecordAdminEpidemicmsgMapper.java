package com.shj.eids.dao;

import com.shj.eids.domain.RecordAdminEpidemicMsg;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: RecordAdminEpidemicmsg
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-05 13:50
 **/
@Repository
@Mapper
public interface RecordAdminEpidemicmsgMapper {
    public void addRecord(RecordAdminEpidemicMsg record);
    /*
     * @Title: getRecords
     * @Description:
     * @param args:key的值可以为：
     *              adminId: 操作者管理员的id
     *              msgId: 被操作文章的id
     *              recordType: 操作的类型
     *              start和length: 分页用的起始位置和长度
     * @return java.util.List<com.shj.eids.dao.RecordAdminEpidemicmsg>
     * @Author: ShangJin
     * @Date: 2020/3/5
     */
    public List<RecordAdminEpidemicMsg> getRecords(Map<String, Object> args);
}
