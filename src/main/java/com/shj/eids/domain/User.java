package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

/**
 * @ClassName: User
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 20:47
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("User")
public class User {
    private Integer id;
    private String email;
    private String password;
    private Integer level;
    private String introduction;
}