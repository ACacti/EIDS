package com.shj.eids.service;

import com.shj.eids.dao.AidInformationMapper;
import com.shj.eids.dao.RecordAdminAidinfoMapper;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.AidInformation;
import com.shj.eids.domain.RecordAdminAidinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName: AidInformationService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-19 14:15
 **/
@Service
public class AidInformationService {

    final static public int UPGRADE = 1;
    final static public int DOWNGRADE = 2;
    final static public int DELETE = 3;

    @Autowired
    private AidInformationMapper aidInformationMapper;

    @Autowired
    private RecordAdminAidinfoMapper recordAdminAidinfoMapper;

    public void addAidInformation(AidInformation info){
        aidInformationMapper.addAidInformation(info);
    }

    public void deleteAidInformation(AidInformation info){
        aidInformationMapper.deleteAidInformation(info);
    }

    @Transactional
    public void deleteAidInformation(List<String> ids){
        Date d = new Date();
        for(String id: ids){
            aidInformationMapper.deleteAidInformation(new AidInformation(id, null, null, null, null, null));
        }
    }

    @Transactional
    public void postAidInformation(AidInformation info){
        aidInformationMapper.addAidInformation(info);
        recordAdminAidinfoMapper.addRecord(new RecordAdminAidinfo(null, info.getPublisher(), info, info.getReleaseTime(), "发布"));
    }

    public List<AidInformation> getAidInformation(String content ,Integer start, Integer length){
        return aidInformationMapper.getInformation(start, length, content);
    }

    @Transactional
    public Integer updateAidInformation(String id, Admin admin, Integer method){
        AidInformation aidInformation = aidInformationMapper.getInformationById(id);
        Integer weight = aidInformation.getWeight();
        switch (method){
            case UPGRADE:{
                if(weight >= 2){
                    return weight;
                }
                aidInformation.setWeight(++weight);
                aidInformationMapper.updateAidInformation(aidInformation);
                recordAdminAidinfoMapper.addRecord(new RecordAdminAidinfo(null, admin, aidInformation, new Date(), "升级"));

                break;
            }
            case DOWNGRADE:
                if(weight <= 1){
                    return weight;
                }
                aidInformation.setWeight(--weight);
                aidInformationMapper.updateAidInformation(aidInformation);
                recordAdminAidinfoMapper.addRecord(new RecordAdminAidinfo(null, admin, aidInformation, new Date(), "降级"));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + method);
        }
        return weight;
    }
    public Integer getCount(String content){
        return aidInformationMapper.getCount(content);
    }


    public List<RecordAdminAidinfo> getAdminAidinformationRecord(@Nullable Integer adminId, Integer start, Integer length){
        Map<String, Object> map = new HashMap<>();
        map.put("adminId", adminId);
        map.put("start", start);
        map.put("length", length);
        return recordAdminAidinfoMapper.getRecords(map);
    }

    public Integer getRecordCount(@Nullable Integer adminId){
        Map<String, Object> map = new HashMap<>();
        map.put("adminId", adminId);
        return recordAdminAidinfoMapper.getCount(map);

    }
}