package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.shj.eids.domain.PatientInformation;
import com.shj.eids.service.EpidemicInfoService;
import com.shj.eids.service.PatientInformationService;
import com.shj.eids.utils.LocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: SearchPatientController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-19 13:52
 **/
@Controller
public class SearchPatientController {
    @Autowired
    private PatientInformationService patientInformationService;
    @Autowired
    private EpidemicInfoService epidemicInfoService;
    @RequestMapping("/user/search")
    public String toSearchPage(Model model){
        model.addAttribute("provinces", EpidemicInfoService.provinces);
        return "search";
    }

    @PostMapping("/user/search/province")
    @ResponseBody
    public String getCities(@RequestParam("province") String province){
        LocalUtil localUtil = LocalUtil.getInstance();
        List<String> cities = localUtil.getCities("中国", province);
        return JSON.toJSONString(cities);
    }

    @PostMapping("/user/search/patientInfo")
    @ResponseBody
    public String getPatientInfo(@RequestParam("limit") Integer limit, @RequestParam("page") Integer page,
                                 @RequestParam("province") String province,
                                 @RequestParam("city") String city){
        Map<String, Object> res = new HashMap<>();
        Integer index = limit * (page - 1);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        List<PatientInformation> list = patientInformationService.getPatientInformation(province, city, null, null, null, index, limit);
        List<Map<String, Object>> data = new ArrayList<>();
        for(PatientInformation p : list){
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("name", p.getName());
            m.put("locationProvince", p.getLocationProvince());
            m.put("locationCity", p.getLocationCity());
            m.put("epidemicName", p.getEpidemicEvent().getName());
            m.put("status", p.getStatus());
            m.put("reportingTime", format.format(p.getReportingTime()));
            data.add(m);
        }
        res.put("data", data);
        res.put("code", 0);
        res.put("msg", "");
        res.put("count", epidemicInfoService.getPatientCount(null, province, city, null, null, null));
        return JSON.toJSONString(res);
    }
}