package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * (RecordAdminManagement)实体类
 *
 * @author Shangjin
 * @since 2020-04-07 13:54:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordAdminManagement implements Serializable {
    private static final long serialVersionUID = -45930663143369473L;
    
    private Integer id;
    
    private Date recordTime;
    
    private Admin managedAdmin;
    
    private Admin processor;
    private String recordType;

}