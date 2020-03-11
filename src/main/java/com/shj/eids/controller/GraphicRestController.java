package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.shj.eids.service.EpidemcInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: GraphicRestController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-11 17:43
 **/
@RestController
public class GraphicRestController {
    @Autowired
    EpidemcInfoService epidemcInfoService;
    @PostMapping("/graphic/mapGraphicDataAll/{epidemicId}")
    public String getCityStatusCountAll(@PathVariable("epidemicId") Integer epidemicId){
        return JSON.toJSONString(epidemcInfoService.getMapDataAll(epidemicId));
    }
    @PostMapping("/graphic/mapGraphicDataPresent/{epidemicId}")
    public String getMapGraphicDataPresent(@PathVariable("epidemicId") Integer epidemicId){
        return JSON.toJSONString(epidemcInfoService.getMapDataPresent(epidemicId));
    }
}