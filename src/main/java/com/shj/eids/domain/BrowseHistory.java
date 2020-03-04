package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @ClassName: BrowseHistory
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 20:53
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("BrowseHistory")
public class BrowseHistory {
    private Integer id;
    private User user;
    private Date browseTime;
}