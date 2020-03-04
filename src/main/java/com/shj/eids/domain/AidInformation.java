package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @ClassName: AidInformation
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 20:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("AidInformation")
public class AidInformation {
    private Integer id;
    private String title;
    private String content;
    private Date releaseTime;
    private Admin publisher;
}