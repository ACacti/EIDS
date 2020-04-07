package com.shj.eids.dao;

import com.shj.eids.domain.RecordAdminManagement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * (RecordAdminManagement)表数据库访问层
 *
 * @author Shangjin
 * @since 2020-04-07 13:56:55
 */
@Repository
@Mapper
public interface RecordAdminManagementMapper {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    RecordAdminManagement queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<RecordAdminManagement> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 新增数据
     *
     * @param recordAdminManagement 实例对象
     * @return 影响行数
     */
    int insert(RecordAdminManagement recordAdminManagement);

    /**
     * 修改数据
     *
     * @param recordAdminManagement 实例对象
     * @return 影响行数
     */
    int update(RecordAdminManagement recordAdminManagement);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    List<RecordAdminManagement> queryByManagedId(@Param("id") int id, @Param("offset") int offset, @Param("length") int length);

    int getCountByManagedId(int id);

    int getCount();
}