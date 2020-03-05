package com.shj.eids.dao;

import com.shj.eids.domain.RecordAdminAidinfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: RecordAdminAidinfoMapper
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-05 13:10
 **/
@Repository
@Mapper
public interface RecordAdminAidinfoMapper {
    public void addRecord(RecordAdminAidinfo record);
    /*
     * @Title: getRecordByAdminId
     * @Description: 根据给定的限制获取管理员对求助信息的操作记录
     * @param args: key的值为数据库record_admin_aidinfo表的列名（转换为驼峰命名规则）
     *              value为要查找的列名对应的值。
     *              除此之外，还可以将key指定为:
     *              start和length: 表示SQL语句中limit后的两个参数
     *              startDate和endDate:表示查询的操作记录的时间区间
     * @return java.util.List<com.shj.eids.domain.RecordAdminAidinfo>
     * @Author: ShangJin
     * @Date: 2020/3/5
     */
    public List<RecordAdminAidinfo> getRecords(Map<String, Object> args);
}
