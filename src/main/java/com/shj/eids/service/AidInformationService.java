package com.shj.eids.service;

import com.shj.eids.dao.AidInformationMapper;
import com.shj.eids.domain.AidInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: AidInformationService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-19 14:15
 **/
@Service
public class AidInformationService {
    @Autowired
    private AidInformationMapper aidInformationMapper;

    public void addAidInformation(AidInformation info){
        aidInformationMapper.addAidInformation(info);
    }

    public void updateAidInformation(AidInformation info){
        aidInformationMapper.updateAidInformation(info);
    }
    public void deleteAidInformation(AidInformation info){
        aidInformationMapper.deleteAidInformation(info);
    }

    public List<AidInformation> getAidInformation(Integer start, Integer length){
        return aidInformationMapper.getInformation(start, length);
    }
    public Integer getCount(){
        return aidInformationMapper.getCount();
    }
}