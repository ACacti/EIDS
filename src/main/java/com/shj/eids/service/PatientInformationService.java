package com.shj.eids.service;

import com.baidu.aip.face.AipFace;
import com.shj.eids.dao.PatientInformationMapper;
import com.shj.eids.domain.PatientInformation;
import com.shj.eids.utils.AipFaceUtils;
import com.shj.eids.utils.Base64Util;
import com.shj.eids.utils.TransferImageUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
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
                                                          Integer epidemicId, Integer start, Integer length, String idNumber, String ...status){
        Map<String, Object> args = new HashMap<>();
        args.put("locationProvince", province);
        args.put("locationCity", city);
        args.put("startTime", startTime);
        args.put("endTime", endTime);
        args.put("epidemicId", epidemicId);
        args.put("start", start);
        args.put("length", length);
        args.put("idNumber", idNumber);
        if(status.length != 0){
            args.put("status", Arrays.asList(status));
        }
        return patientInformationMapper.getPatientInformation(args);
    }

    public Integer getPatientInformationCount(String province, String city, Date startTime, Date endTime,
                                                          Integer epidemicId, Integer start, Integer length, String idNumber, String ...status){
        Map<String, Object> args = new HashMap<>();
        args.put("locationProvince", province);
        args.put("locationCity", city);
        args.put("startTime", startTime);
        args.put("endTime", endTime);
        args.put("epidemicId", epidemicId);
        args.put("start", start);
        args.put("length", length);
        args.put("idNumber", idNumber);
        if(status.length != 0){
            args.put("status", Arrays.asList(status));
        }
        return patientInformationMapper.getCount(args);
    }

    @Transactional
    public void deletePatientInformationById(List<Integer> ids){
        for(Integer id: ids){
            patientInformationMapper.deletePatientInformationById(id);
        }
    }

    public PatientInformation getPatientInformationByIdNumber(String idNumber){
        return patientInformationMapper.getPatientInformationByIdNumber(idNumber);
    }

    public PatientInformation getPatientInformationById(Integer id){
        return patientInformationMapper.getPatientInformationById(id);
    }
    public void updatePatientInformation(PatientInformation info){
        patientInformationMapper.updatePatientInformation(info);
    }

    public JSONObject registerFace(InputStream img, Integer patientId) throws IOException {
        String imageCode = Base64Util.encode(img);
        AipFace client = AipFaceUtils.getClient();
        HashMap<String, String> options = new HashMap<>();
        options.put("action_type ", "REPLACE");
        return client.addUser(imageCode, "BASE64", AipFaceUtils.GROUP_ID, patientId.toString(),options);
    }

    public JSONObject detectFace(InputStream img) throws IOException {
        String imageCode = Base64Util.encode(img);
        AipFace client = AipFaceUtils.getClient();
        HashMap<String, String> options = new HashMap<>();
        options.put("face_field", "quality");//质量检测
        return client.detect(imageCode, "BASE64", options);
    }
}