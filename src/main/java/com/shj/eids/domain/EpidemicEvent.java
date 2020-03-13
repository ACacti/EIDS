package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @ClassName: EpidemicEvent
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-07 14:52
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("EpidemicEvent")
public class EpidemicEvent {
    private Integer id;
    private String name;
    private Date releaseTime;
    private Admin publisher;
}