package com.shj.eids.service;

import com.shj.eids.dao.AidInformationMapper;
import com.shj.eids.dao.RecordAdminAidinfoMapper;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.AidInformation;
import com.shj.eids.domain.RecordAdminAidinfo;
import com.shj.eids.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.validation.constraints.Email;
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

    @Autowired
    private EmailUtil emailUtil;

    public void addAidInformation(AidInformation info){
        aidInformationMapper.addAidInformation(info);
    }

    public void deleteAidInformation(AidInformation info){
        aidInformationMapper.deleteAidInformation(info);
    }

    @Transactional
    public void deleteAidInformation(List<String> ids, Admin admin) throws MessagingException {
        for(String id: ids){
            AidInformation temp = aidInformationMapper.getInformationById(id);
            aidInformationMapper.deleteAidInformation(temp);
            String text = "EIDS管理员，您的标题为<strong>" + temp.getTitle() + "</strong>的援助请求被管理员删除，" +
                    "若有疑问请联系管理员邮箱<em> " + admin.getEmail() + " </em>";
            //发送邮件费时，所以放到异步任务中
            emailUtil.sendComplexEmailByAsynchronousMode(text, temp.getPublisher().getEmail(), "援助请求删除");
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