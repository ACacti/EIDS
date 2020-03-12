package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @ClassName: EveryDayCount
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-12 22:46
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("EveryDayCount")
public class EveryDayCount {
    private Integer id;
    private Integer epidemicEventId;
    private String province;
    private Date date;
    private String status;
    private Integer amount;
}