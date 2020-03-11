package com.shj.eids.service;

import com.shj.eids.dao.EpidemicEventMapper;
import com.shj.eids.dao.PatientInformationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.awt.geom.AreaOp;

import java.util.*;

/**
 * @ClassName: EpidemcInfoService
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-10 22:07
 **/
@Service
public class EpidemcInfoService {
    @Autowired
    private EpidemicEventMapper eventMapper;
    @Autowired
    private PatientInformationMapper patientInformationMapper;

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
     * @Title: getPresentPatientCount
     * @Description: 获取今天零时到现在新增的确诊人数
     * @param epidemicId: 疫情事件id
     * @param province: 所在省
     * @param city: 所在市（直辖市为空）
     * @param status: 患者状态
     * @return java.lang.Integer
     * @Author: ShangJin
     * @Date: 2020/3/11
     */
    public Integer getIncreasedCountToday(Integer epidemicId, String province, String city){
        Calendar cal = Calendar.getInstance();
        Date endTime = cal.getTime();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startTime = cal.getTime();
        return getPatientCount(epidemicId, province, city, null, startTime, endTime);
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
    class MapDataItem{
        String name;
        Integer value;

        public MapDataItem(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public MapDataItem() {
        }

        public String getName(){
            return name;
        }
        public void setName(String name){
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
    public List<MapDataItem> getMapDataAll(Integer epidemicId){
        List<MapDataItem> list = new ArrayList<>();
        for(String province: provinces){
            Integer value = getAllPatientCount(epidemicId, province, null);
            list.add(new MapDataItem(province, value));
        }
        return list;
    }

    public List<MapDataItem> getMapDataPresent(Integer epidemicId){
        List<MapDataItem> list = new ArrayList<>();
        List<String> status = new ArrayList<>();
        status.add("轻微");
        status.add("危重");
        for(String province: provinces){
            Integer value = getPatientCountByStatus(epidemicId, province, null,status);
            list.add(new MapDataItem(province, value));
        }
        return list;
    }

}