package com.shj.eids.controller;

import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.service.EpidemicEventService;
import com.shj.eids.service.EpidemicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
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
    @Autowired
    EpidemicInfoService infoService;
    /*
     * 访问主页，默认显示第一个疫情事件的全国数据
     */
    @RequestMapping("/index")
    String redirectTo(Model model){
        List<EpidemicEvent> list = epidemicEventService.getAllEvent();
        EpidemicEvent event = list.get(0);
        return "forward:index/" + event.getName() + "/china";
    }

    /*
     * 访问某个疫情事件的全国数据
     */
    @RequestMapping("/index/{epidemicName}/{regionName}")
    String preIndexProcedures(@PathVariable("epidemicName") String name,
                              @PathVariable("regionName") String regionName,
                              Model model){
        List<EpidemicEvent> list = epidemicEventService.getAllEvent();
        EpidemicEvent event = epidemicEventService.getEpidemicEventByName(name);
        if(event == null){
            return "redirect:/index";
        }
        model.addAttribute("events", list);
        model.addAttribute("epidemic", event);
        model.addAttribute("regionName", regionName);
        String provinceArg = "china".equals(regionName) ? null: regionName;
        model.addAttribute("allConfirmed", infoService.getAllPatientCount(event.getId(), provinceArg, null));
        List<String> status = Arrays.asList("轻微", "危重");
        model.addAttribute("present", infoService.getPatientCountByStatus(event.getId(), provinceArg,null,status));
        model.addAttribute("allCured", infoService.getPatientCountByStatus(event.getId(), provinceArg, null, "治愈"));
        model.addAttribute("allDead", infoService.getPatientCountByStatus(event.getId(), provinceArg, null, "死亡") );
        return "index";
    }
}