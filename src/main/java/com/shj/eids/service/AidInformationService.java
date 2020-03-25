package com.shj.eids.service;

import com.shj.eids.dao.AidInformationMapper;
import com.shj.eids.dao.RecordAdminAidinfoMapper;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.AidInformation;
import com.shj.eids.domain.RecordAdminAidinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        for(String id: ids){
            aidInformationMapper.deleteAidInformation(new AidInformation(id, null, null, null, null, null));
        }
    }

    @Transactional
    public void postAidInformation(AidInformation info){
        aidInformationMapper.addAidInformation(info);
        recordAdminAidinfoMapper.addRecord(new RecordAdminAidinfo(null, info.getPublisher(), info, info.getReleaseTime(), "发布"));
    }

    public List<AidInformation> getAidInformation(Integer start, Integer length){
        return aidInformationMapper.getInformation(start, length);
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
    public Integer getCount(){
        return aidInformationMapper.getCount();
    }
}