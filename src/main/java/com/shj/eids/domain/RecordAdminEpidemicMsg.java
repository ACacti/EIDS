package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @ClassName: RecordAdminEpidemicMsg
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 21:23
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("RecordAdminEpidemicMsg")
public class RecordAdminEpidemicMsg {
    private Integer id;
    private Admin admin;
    private EpidemicMsg msg;
    private Date recordTime;
    private String recordType;

}