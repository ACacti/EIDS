package com.shj.eids.controller;

import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.service.EpidemicEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @ClassName: IndexController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-10 13:14
 **/
@Controller
public class IndexController {
    @Autowired
    EpidemicEventService epidemicEventService;
    @RequestMapping("/index")
    String redirectTo(Model model){
        List<EpidemicEvent> list = epidemicEventService.getAllEvent();
        EpidemicEvent event = list.get(0);
        model.addAttribute("events", list);
        model.addAttribute("epidemic", event);
        return "index";
    }

    @RequestMapping("/index/{epidemicName}")
    String preIndexProcedures(@PathVariable("epidemicName") String name, Model model){
        List<EpidemicEvent> list = epidemicEventService.getAllEvent();
        EpidemicEvent event = epidemicEventService.getEpidemicEventByName(name);
        if(event == null){
            return "redirect:/index";
        }
        model.addAttribute("events", list);
        model.addAttribute("epidemic", event);
        return "index";
    }
}