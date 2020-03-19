package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.shj.eids.domain.AidInformation;
import com.shj.eids.domain.EpidemicMsg;
import com.shj.eids.service.AidInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: GetHelpRestController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-19 14:47
 **/
@RestController
public class GetHelpRestController {
    @Autowired
    private AidInformationService aidInformationService;
    @PostMapping("/user/help/{page}")
    public String getArticle(@PathVariable("page") Integer page){
        Map<String, Object> res = new HashMap<>();
        try {
            Integer num = aidInformationService.getCount();
            Integer pageNum = num % 8 == 0 ? num / 8 : num / 8 + 1;//总页数
            res.put("pages", pageNum);
            List<AidInformation> list = aidInformationService.getAidInformation((page - 1) * 8, 8);
            res.put("data",list);
            res.put("msg", "success");
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }
    }
}