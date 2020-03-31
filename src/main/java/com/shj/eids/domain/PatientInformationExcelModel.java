package com.shj.eids.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @ClassName: PatientInformationExcelModel
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-31 17:36
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientInformationExcelModel {
    private String name;
    private String locationProvince;
    private String locationCity;
    private String locationDetail;
    private Integer eventId;
    private String status;
    private String idNumber;
    private Integer id;
    private Date reportingTime;
}