package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.shj.eids.domain.DataItem;
import com.shj.eids.service.EpidemicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: GraphicRestController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-11 17:43
 **/
@RestController
public class GraphicRestController {
    @Autowired
    EpidemicInfoService epidemicInfoService;
    /*
     * @Title: getCityStatusCountAll
     * @Description: 根据疫情事件ID获取累计疫情疫情地图需要的数据
     * @param epidemicId: 
     * @return java.lang.String
     * @Author: ShangJin
     * @Date: 2020/3/12
     */
    @PostMapping("/graphic/mapGraphicDataAll/{epidemicId}")
    public String getCityStatusCountAll(@PathVariable("epidemicId") Integer epidemicId){
        return JSON.toJSONString(epidemicInfoService.getMapDataAll(epidemicId));
    }
    /*
     * @Title: getMapGraphicDataPresent
     * @Description: 根据疫情事件ID获取当前疫情疫情地图需要的数据
     * @param epidemicId: 
     * @return java.lang.String
     * @Author: ShangJin
     * @Date: 2020/3/12
     */
    @PostMapping("/graphic/mapGraphicDataPresent/{epidemicId}")
    public String getMapGraphicDataPresent(@PathVariable("epidemicId") Integer epidemicId){
        return JSON.toJSONString(epidemicInfoService.getMapDataPresent(epidemicId));
    }

    /*
     * @Title: getPieGraphic
     * @Description: 根据疫情事件ID获取饼图需要的数据：死亡人数、治愈人数、危重人数、轻微人数
     * @param epidemicId:
     * @return java.lang.String
     * @Author: ShangJin
     * @Date: 2020/3/12
     */
    @PostMapping("/graphic/pieGraphic/{epidemicId}")
    public String getPieGraphic(@PathVariable("epidemicId") Integer epidemicId){
        ArrayList<DataItem> res = new ArrayList<>();
        epidemicInfoService.getPatientCountByStatus(epidemicId, null, null, "死亡");
        res.add(new DataItem("死亡人数",  epidemicInfoService.getPatientCountByStatus(epidemicId, null, null, "死亡")));
        res.add(new DataItem("治愈人数",  epidemicInfoService.getPatientCountByStatus(epidemicId, null, null, "治愈")));
        res.add(new DataItem("危重人数",  epidemicInfoService.getPatientCountByStatus(epidemicId, null, null, "危重")));
        res.add(new DataItem("轻微人数",  epidemicInfoService.getPatientCountByStatus(epidemicId, null, null, "轻微")));
        return JSON.toJSONString(res);
    }

    @PostMapping("/graphic/pieGraphic/{epidemicId}/{province}")
    public String getPieGraphicByProvince(@PathVariable("epidemicId") Integer epidemicId, @PathVariable("province") String province){
        ArrayList<DataItem> res = new ArrayList<>();
        epidemicInfoService.getPatientCountByStatus(epidemicId, null, null, "死亡");
        res.add(new DataItem("死亡人数",  epidemicInfoService.getPatientCountByStatus(epidemicId, province, null, "死亡")));
        res.add(new DataItem("治愈人数",  epidemicInfoService.getPatientCountByStatus(epidemicId, province, null, "治愈")));
        res.add(new DataItem("危重人数",  epidemicInfoService.getPatientCountByStatus(epidemicId, province, null, "危重")));
        res.add(new DataItem("轻微人数",  epidemicInfoService.getPatientCountByStatus(epidemicId, province, null, "轻微")));
        return JSON.toJSONString(res);
    }

    @PostMapping("/graphic/lineAndBarGraphic/{epidemicId}")
    public String getBarAndLineGraphicData(@PathVariable("epidemicId") Integer epidemicId){
        final Date startTime = epidemicInfoService.getStartTime(epidemicId);
        Date now = new Date();
        List<Object> increased = epidemicInfoService.getIncreasedPatientCount(epidemicId, null, startTime, now);
        List<Object> confirmed = epidemicInfoService.getConfirmedPatientCount(epidemicId, null, startTime, now);
        List<Object> dead = epidemicInfoService.getDeadPatientCount(epidemicId, null, startTime, now);
        List<List<Object>> res = new ArrayList<>();
        res.add(increased);
        res.add(confirmed);
        res.add(dead);
        return JSON.toJSONString(res);
    }

    @PostMapping("/graphic/lineAndBarGraphic/{epidemicId}/{province}")
    public String getBarAndLineGraphicDataByProvince(@PathVariable("epidemicId") Integer epidemicId, @PathVariable("province") String province){
        final Date startTime = epidemicInfoService.getStartTime(epidemicId);
        Date now = new Date();
        List<Object> increased = epidemicInfoService.getIncreasedPatientCount(epidemicId, province, startTime, now);
        List<Object> confirmed = epidemicInfoService.getConfirmedPatientCount(epidemicId, province, startTime, now);
        List<Object> dead = epidemicInfoService.getDeadPatientCount(epidemicId, province, startTime, now);
        List<List<Object>> res = new ArrayList<>();
        res.add(increased);
        res.add(confirmed);
        res.add(dead);
        return JSON.toJSONString(res);
    }
}