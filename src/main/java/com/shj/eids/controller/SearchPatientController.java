package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.domain.PatientInformation;
import com.shj.eids.service.EpidemicEventService;
import com.shj.eids.service.EpidemicInfoService;
import com.shj.eids.service.PatientInformationService;
import com.shj.eids.utils.LocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private EpidemicEventService epidemicEventService;

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
        List<PatientInformation> list = patientInformationService.getPatientInformation(province, city, null, null, null, index, limit, null);
        //将身份证号设为空
        for(PatientInformation info : list){
            info.setIdNumber("");
        }
        res.put("data", list);
        res.put("code", 0);
        res.put("msg", "");
        res.put("count", epidemicInfoService.getPatientCount(null, province, city, null, null, null));
        return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
    }

    @PostMapping("/user/search/uploadImg")
    @ResponseBody
    public String faceSearch(MultipartFile file){
        Map<String, Object> res = new HashMap<>();
        try {
            List<EpidemicEvent> events = epidemicEventService.getAllEvent();
            List<String> eventIdList = new ArrayList<>();
            //获取所有疫情事件ID
            for(EpidemicEvent event : events){
                eventIdList.add(event.getId().toString());
            }
            //将图片在所有疫情事件组中查找
            List<Double> score = new ArrayList<>();
            List<PatientInformation> patientInformation = patientInformationService.faceSearch(file.getInputStream(), eventIdList, score);
            //获取到的患者信息直接加入数据表格，前端数据表格无法进行异步返回接口的解析，所以在后台就要将数据封装成前端能解析的格式
            List<Map<String, Object>> data = new ArrayList<>();
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Double e = 1.2;
            for(int i = 0; i < patientInformation.size(); i++){
                PatientInformation p = patientInformation.get(i);
                Map<String, Object> m = new HashMap<>();
                m.put("id", p.getId());
                m.put("name", p.getName());
                m.put("locationProvince", p.getLocationProvince());
                m.put("locationCity", p.getLocationCity());
                m.put("epidemicName", p.getEpidemicEvent().getName());
                m.put("status", p.getStatus());
                m.put("reportingTime", format.format(p.getReportingTime()));
                m.put("score", score.get(i));
                data.add(m);
            }
            res.put("data", data);
            res.put("code", 0);
            res.put("count", patientInformation.size());
            return JSON.toJSONString(res);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 1);
            res.put("msg", e.getMessage());
            return JSON.toJSONString(res);
        }
    }
}