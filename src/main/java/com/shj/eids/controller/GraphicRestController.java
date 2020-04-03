package com.shj.eids.controller;

import com.alibaba.fastjson.JSON;
import com.shj.eids.domain.DataItem;
import com.shj.eids.service.EpidemicInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
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
    @PostMapping("/graphic/mapGraphicData/{epidemicId}/{regionName}")
    public String getCityStatusCountAll(@PathVariable("epidemicId") Integer epidemicId,
                                        @PathVariable("regionName") String regionName){
        String province = ("china".equals(regionName) ? null: regionName);
        List<DataItem> mapDataAll = epidemicInfoService.getMapDataAll(epidemicId, province);
        List<DataItem> mapDataPresent = epidemicInfoService.getMapDataPresent(epidemicId, province);
        return JSON.toJSONString(Arrays.asList(mapDataAll, mapDataPresent));
    }
    /*
     * @Title: getPieGraphic
     * @Description: 根据疫情事件ID获取饼图需要的数据：死亡人数、治愈人数、危重人数、轻微人数
     * @param epidemicId:
     * @return java.lang.String
     * @Author: ShangJin
     * @Date: 2020/3/12
     */
    @PostMapping("/graphic/pieGraphic/{epidemicId}/{regionName}")
    public String getPieGraphic(@PathVariable("epidemicId") Integer epidemicId,
                                @PathVariable("regionName") String regionName){
        String province = (regionName.equals("china")? null:regionName);
        ArrayList<DataItem> res = new ArrayList<>();
        epidemicInfoService.getPatientCountByStatus(epidemicId, null, null, "死亡");
        res.add(new DataItem("死亡",  epidemicInfoService.getPatientCountByStatus(epidemicId, province, null, "死亡")));
        res.add(new DataItem("治愈",  epidemicInfoService.getPatientCountByStatus(epidemicId, province, null, "治愈")));
        res.add(new DataItem("危重",  epidemicInfoService.getPatientCountByStatus(epidemicId, province, null, "危重")));
        res.add(new DataItem("轻微",  epidemicInfoService.getPatientCountByStatus(epidemicId, province, null, "轻微")));
        return JSON.toJSONString(res);
    }

    /*
     * 获取折线图和柱状图所需要的数据,
     */
    @PostMapping("/graphic/lineAndBarGraphic/{epidemicId}/{regionName}")
    public String getBarAndLineGraphicData(@PathVariable("epidemicId") Integer epidemicId,
                                           @PathVariable("regionName") String regionName){
        String province = ("china".equals(regionName)? null: regionName);
        List<List<Object>> res = epidemicInfoService.getLineAndBarGraphicData(epidemicId, province);
        return JSON.toJSONString(res);
    }

    @PostMapping("/graphic/lineGraphic/{epidemicId}/{regionName}")
    public String getRegionPatientCount(@PathVariable("epidemicId") Integer epidemicId,
                                        @PathVariable("regionName") String regionName){
        String province = ("china".equals(regionName)? null: regionName);
        List<List<Object>> res = epidemicInfoService.getRegionPatientCountData(epidemicId, province);
        return JSON.toJSONString(res);
    }
}