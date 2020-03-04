package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @ClassName: RecordAdminUser
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 21:25
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("RecordAdminUser")
public class RecordAdminUser {
    private Integer id;
    private Admin admin;
    private User user;
    private Date recordTime;
    private String recordType;
}