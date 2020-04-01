package com.shj.eids.service;

import com.shj.eids.dao.EpidemicEventMapper;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.utils.AipFaceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.validation.constraints.Null;
import java.net.Inet4Address;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EpidemicEventService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-11 21:25
 **/
@Service
public class EpidemicEventService {
    @Autowired
    private EpidemicEventMapper eventMapper;
    public List<EpidemicEvent> getAllEvent(){
        Map<String, Object> args = new HashMap<>();
        return eventMapper.getEpidemicEvents(args);
    }

    public EpidemicEvent getEpidemicEventByName(String name){
        Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        return eventMapper.getEpidemicEvents(args).get(0);
    }

    public List<EpidemicEvent> getEvents(@Nullable Integer id, @Nullable String name, @Nullable Integer publisherId,
                                         @Nullable Integer start, @Nullable Integer length){
        Map<String, Object> args = new HashMap<>();
        args.put("id", id);
        args.put("name", name);
        args.put("publisherId", publisherId);
        args.put("start", start);
        args.put("length", length);
        return eventMapper.getEpidemicEvents(args);
    }

    public Integer getAllCount(){
        return eventMapper.getCount();
    }

    @Transactional
    public void addEpidemicEvent(Admin admin, String name) throws Exception {
        EpidemicEvent event = new EpidemicEvent(null, name, new Date(), admin);
        eventMapper.addEpidemicEvent(event);
        Map<String, Object> args = new HashMap<>();
        args.put("name", name);
        //获取刚刚添加的事件的ID(因为事件ID是数据库自增，创建后才能获取...当初设计的失败)
        Integer eventId = eventMapper.getEpidemicEvents(args).get(0).getId();
        //新建人脸库
        AipFaceUtils.addGroup(eventId.toString());

    }

    @Transactional
    public void deleteEpidemicEventById(Integer id) throws Exception {
        eventMapper.deleteEpidemicEventById(id);
        //删除人脸库
        AipFaceUtils.deleteGroup(id.toString());
    }

    public void updateEpidemicEvent(EpidemicEvent epidemicEvent){
        eventMapper.updateEpidemicEvent(epidemicEvent);
    }
}