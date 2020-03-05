package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @ClassName: EpidemicMsg
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 20:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("EpidemicMsg")
public class EpidemicMsg {
    private Integer id;
    private String tile;
    private String content;
    private Date releaseDate;
    private Integer weight;
    private User author;
}