package com.shj.eids.service;

import com.shj.eids.dao.EpidemicEventMapper;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.EpidemicEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
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

    public void addEpidemicEvent(Admin admin, String name){
        EpidemicEvent event = new EpidemicEvent(null, name, new Date(), admin);
        eventMapper.addEpidemicEvent(event);
    }

    public void deleteEpidemicEventById(Integer id){
        eventMapper.deleteEpidemicEventById(id);
    }

    public void updateEpidemicEvent(EpidemicEvent epidemicEvent){
        eventMapper.updateEpidemicEvent(epidemicEvent);
    }
}