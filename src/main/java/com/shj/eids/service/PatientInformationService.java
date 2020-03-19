package com.shj.eids.service;

import com.shj.eids.dao.PatientInformationMapper;
import com.shj.eids.domain.PatientInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: PatientInformationService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-19 19:17
 **/
@Service
public class PatientInformationService {
    @Autowired
    private PatientInformationMapper patientInformationMapper;

    public List<PatientInformation> getPatientInformation(String province, String city, Date startTime, Date endTime,
                                                          Integer epidemicId, Integer start, Integer length, String ...status){
        Map<String, Object> args = new HashMap<>();
        args.put("locationProvince", province);
        args.put("locationCity", city);
        args.put("startTime", startTime);
        args.put("endTime", endTime);
        args.put("epidemicId", epidemicId);
        args.put("start", start);
        args.put("length", length);
        if(status.length != 0){
            args.put("status", Arrays.asList(status));
        }
        return patientInformationMapper.getPatientInformation(args);
    }
}