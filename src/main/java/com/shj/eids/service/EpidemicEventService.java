package com.shj.eids.service;

import com.shj.eids.dao.EpidemicEventMapper;
import com.shj.eids.domain.EpidemicEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}