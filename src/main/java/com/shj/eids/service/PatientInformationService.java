package com.shj.eids.service;

import com.baidu.aip.face.AipFace;
import com.shj.eids.dao.PatientInformationMapper;
import com.shj.eids.domain.PatientInformation;
import com.shj.eids.domain.PatientInformationExcelModel;
import com.shj.eids.utils.AipFaceUtils;
import com.shj.eids.utils.Base64Util;
import com.shj.eids.utils.TransferImageUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
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

    /*
     * @Title: registerFace
     * @Description: 获取人脸照片的输入流，完成注册人脸功能，返回一个注册结果
     * @param img: 人脸照片输入流
     * @param patientId: 要注册的患者ID信息
     * @return org.json.JSONObject：注册结果
     * @Author: ShangJin
     * @Date: 2020/4/1
     */
    public JSONObject registerFace(InputStream img, Integer patientId, Integer eventId) throws IOException {
        return AipFaceUtils.registerFace(img, patientId.toString(), eventId.toString());
    }

    /*
     * @Title: detectFace
     * @Description: 获取人脸照片输入流，检查人脸照片是否合格
     * @param img: 人脸照片输入流
     * @return org.json.JSONObject：检测结果
     * @Author: ShangJin
     * @Date: 2020/4/1
     */
    public JSONObject detectFace(InputStream img) throws IOException {
        return AipFaceUtils.detectFace(img);
    }

    public List<PatientInformation> faceSearch(InputStream file, List<String> eventIds, @Nullable List<Double> score) throws Exception {

        List<String> patientIdList =  AipFaceUtils.faceSearch(file, eventIds, score);
        List<PatientInformation> res = new ArrayList<>();
        for(String id: patientIdList){
            res.add(patientInformationMapper.getPatientInformationById(Integer.parseInt(id)));
        }
        return res;
    }

    /*
     * 批量导入患者信息
     */
    @Transactional
    public void addPatientInformationByExcelModel(List<PatientInformationExcelModel> list){
        for(PatientInformationExcelModel model : list){
            patientInformationMapper.addPatientInformationByExcelModel(model);
        }
    }


}