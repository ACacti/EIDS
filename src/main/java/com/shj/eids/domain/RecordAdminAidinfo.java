package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @ClassName: RecordAdminAidinfo
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 21:01
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("RecordAdminAidinfo")
public class RecordAdminAidinfo {
    private Integer id;
    private Admin admin;
    private AidInformation aidInformation;
    private Date recordTime;
    private String recordType;
}