package com.shj.eids.domain;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.util.Date;

/**
 * @ClassName: PatientInformation
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-04 20:58
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Alias("PatientInformation")
public class PatientInformation {
    private Integer id;
    private String name;
    private String locationProvince;
    private String locationCity;
    private String locationDetail;
    private String faceData;
    private Date reportingTime;
    private EpidemicEvent epidemicEvent;
    private String status;
    private String idNumber;
}