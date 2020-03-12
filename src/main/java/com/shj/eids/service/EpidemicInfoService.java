package com.shj.eids.service;

import com.shj.eids.dao.EpidemicEventMapper;
import com.shj.eids.dao.EveryDayCountMapper;
import com.shj.eids.dao.PatientInformationMapper;
import com.shj.eids.domain.DataItem;
import com.shj.eids.domain.EpidemicEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: EpidemcInfoService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-10 22:07
 **/
@Service
public class EpidemicInfoService {
    @Autowired
    private EpidemicEventMapper eventMapper;
    @Autowired
    private PatientInformationMapper patientInformationMapper;
    @Autowired
    private EveryDayCountMapper everyDayCountMapper;

    static private String []provinces={"南海诸岛", "北京", "天津", "上海", "重庆", "河北",
    "河南", "云南", "辽宁", "黑龙江", "湖南", "安徽", "山东","新疆", "江苏", "浙江", "江西",
    "湖北", "广西", "甘肃", "山西", "内蒙古", "陕西", "吉林", "福建", "贵州", "广东", "青海", "西藏",
    "四川", "宁夏", "海南", "台湾", "香港", "澳门"};

    /*
     * @Title: getPresentPatientCount
     * @Description: 获取现有确诊人数，这个函数有最详尽的参数，共其它方法调用，不需要的参数设为null
     * @param epidemicId: 疫情事件id    
	 * @param province: 所在省
	 * @param city: 所在市（直辖市为空）
	 * @param status: List<String>类型，表示患者状态，可用取值为：轻微 危重 死亡 治愈
	 * @param startTime: 查询的时间区间的开始时间
	 * @param endTime: 查询的时间区间的结束时间
     * @return java.lang.Integer
     * @Author: ShangJin
     * @Date: 2020/3/11
     */
    public Integer getPatientCount(Integer epidemicId, String province, String city, List<String> status, Date startTime, Date endTime){
        Map<String, Object> args = new HashMap<>();
        args.put("epidemicId", epidemicId);
        args.put("locationProvince", province);
        args.put("locationCity", city);
        args.put("status", status);
        args.put("startTime", startTime);
        args.put("endTime", endTime);
        return patientInformationMapper.getCount(args);
    }

    /*
     * @Title: getAllPatientCount
     * @Description: 获取所有被确诊过的人数
     * @param epidemicId: 疫情事件id
	 * @param province: 所在省
	 * @param city: 所在市
     * @return java.lang.Integer
     * @Author: ShangJin
     * @Date: 2020/3/11
     */
    public Integer getAllPatientCount(Integer epidemicId, String province, String city){
        return getPatientCount(epidemicId, province, city, null, null, null);
    }

    /*
     * @Title: getPatientCountByStatus
     * @Description: 获取相应状态的患者的数目
     * @param epidemicId: 疫情事件的ID
	 * @param province: 所在省
	 * @param city: 所在市
	 * @param status: List<String>类型，表示患者状态，可用取值为：轻微 危重 死亡 治愈
     * @return java.lang.Integer
     * @Author: ShangJin
     * @Date: 2020/3/11
     */
    public Integer getPatientCountByStatus(Integer epidemicId, String province, String city, List<String> status){
        return getPatientCount(epidemicId, province, city, status, null, null);
    }
    public List<DataItem> getMapDataAll(Integer epidemicId){
        List<DataItem> list = new ArrayList<>();
        for(String province: provinces){
            Integer value = getAllPatientCount(epidemicId, province, null);
            list.add(new DataItem(province, value));
        }
        return list;
    }

    /*
     * 获取当前确诊患者分布疫情地图的数据
     */
    public List<DataItem> getMapDataPresent(Integer epidemicId){
        List<DataItem> list = new ArrayList<>();
        List<String> status = new ArrayList<>();
        status.add("轻微");
        status.add("危重");
        for(String province: provinces){
            Integer value = getPatientCountByStatus(epidemicId, province, null,status);
            list.add(new DataItem(province, value));
        }
        return list;
    }

    /*
     * 获取现在状态为status的患者的数量
     */
    public Integer getPatientCountByStatus(Integer epidemicId, String province, String city, String status){
        List<String> list = new ArrayList<>();
        list.add(status);
        return getPatientCount(epidemicId, province, city, list, null, null);
    }


    /*
     * 获取疫情事件的开始时间
     */
    public Date getStartTime(Integer epidemicId){
        Map<String, Object> args = new HashMap<>();
        args.put("id", epidemicId);
        EpidemicEvent event = eventMapper.getEpidemicEvents(args).get(0);
        if(event != null){
            return event.getReleaseTime();
        }
        return new Date();
    }

    /*
     * 获取一段时间区间内，当天的累计确诊人数统计
     * 返回格式： [yyyy-MM-dd, n1, n2, ...]
     */
    public List<Object> getConfirmedPatientCount(Integer epidemicId, String province, Date s, Date e){
        return null;
    }
    /*
     * 获取一段时间区间内，累计死亡人数统计
     * 返回格式： [yyyy-MM-dd, n1, n2, ...]
     */
    public List<Object> getDeadPatientCount(Integer epidemicId, String province, Date s, Date e){
        return null;
    }
    /*
     * 获取一段时间区间内，每天新增确诊人数统计
     * 返回格式： [yyyy-MM-dd, n1, n2, ...]
     */
    public List<Object> getIncreasedPatientCount(Integer epidemicId, String province, Date s, Date e){
        return null;
    }

}