package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

/**
 * @ClassName: admin
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 20:49
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("Admin")
public class Admin {
    private Integer id;
    private String email;
    private String password;
    private Integer level;
}