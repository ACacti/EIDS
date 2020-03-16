package com.shj.eids.service;

import com.shj.eids.dao.EpidemicEventMapper;
import com.shj.eids.dao.EveryDayCountMapper;
import com.shj.eids.dao.PatientInformationMapper;
import com.shj.eids.domain.DataItem;
import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.utils.LocalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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
    Logger loger = LoggerFactory.getLogger(getClass());

    final static public String []provinces={"南海诸岛", "北京", "天津", "上海", "重庆", "河北",
    "河南", "云南", "辽宁", "黑龙江", "湖南", "安徽", "山东","新疆", "江苏", "浙江", "江西",
    "湖北", "广西", "甘肃", "山西", "内蒙古", "陕西", "吉林", "福建", "贵州", "广东", "青海", "西藏",
    "四川", "宁夏", "海南", "台湾", "香港", "澳门"};
    final static public String []status = {"治愈", "轻微", "危重", "死亡"};

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
    public Integer getAllPatientCount(@NonNull Integer epidemicId, @Nullable String province, @Nullable String city){
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

    /*
     * 获取累计确诊人数疫情地图数据
     * province参数表示要查询的省， province为null时表示获取的是全国疫情地图的数据
     */
    public List<DataItem> getMapDataAll(Integer epidemicId, @Nullable String province){
        List<DataItem> list = new ArrayList<>();
        if(province == null){
            //查询所有省的累计确诊人数
            for(String p: provinces){
                Integer value = getAllPatientCount(epidemicId, p, null);
                list.add(new DataItem(p, value));
            }
        }else{
            //查询所在省各市的累计确诊人数
            LocalUtil instance = LocalUtil.getInstance();
            List<String> cities = instance.getCities("中国", province);
            for(String city: cities){
                Integer value = getAllPatientCount(epidemicId, province, city);
                list.add(new DataItem(city, value));
            }
        }
        return list;
    }

    /*
     * 获取patient_information表当前确诊患者分布疫情地图的数据
     * province不为空时，表示获取某省的当前确诊患者数目疫情地图的数据
     * province为空时，表示获取全国的疫情地图数据
     */
    public List<DataItem> getMapDataPresent(@NonNull Integer epidemicId, @Nullable String province){
        List<DataItem> list = new ArrayList<>();
        List<String> status = new ArrayList<>();
        status.add("轻微");
        status.add("危重");
        if(province==null){
            //获取全国的疫情地图数据
            for(String p: provinces){
                Integer value = getPatientCountByStatus(epidemicId, p, null,status);
                list.add(new DataItem(p, value));
            }
        }else{
            LocalUtil instance = LocalUtil.getInstance();
            List<String> cities = instance.getCities("中国", province);
            for(String city: cities){
                list.add(new DataItem(city, getPatientCountByStatus(epidemicId, province, city, status)));
            }
        }
        return list;
    }

    /*
     * 获取patient_information表现在状态为status的患者的数量
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
     * 对everyDayCountMapper.getIntervalCount()方法的一个包装
     * 获取一段时间内，满足status要求的患者数量，统计以每天为单位，List<Integer>形式返回
     */
    public List<Integer> getIntervalCount(Integer epidemicId, String province, Date startDate, Date endDate, String ...status){
        Map<String, Object> args = new HashMap<>();
        args.put("epidemicEventId", epidemicId);
        args.put("province", province);
        args.put("startDate", startDate);
        args.put("endDate", endDate);
        args.put("status", Arrays.asList(status));
        return everyDayCountMapper.getIntervalCount(args);
    }
    /*
     * 返回折线和柱状图需要的数据
     */
    public List<List<Object>> getLineAndBarGraphicData(Integer epidemicId, String province){
        Date startDate = getStartTime(epidemicId);
        Date now = new Date();
        //获取各种状态患者数目
        List<Integer> deadDates = getIntervalCount(epidemicId, province, startDate,    now, "死亡");
        List<Integer> curedDates = getIntervalCount(epidemicId, province, startDate,   now, "治愈");
        List<Integer> mildDates = getIntervalCount(epidemicId, province, startDate,    now, "轻微");
        List<Integer> seriousDates = getIntervalCount(epidemicId, province, startDate, now, "危重");
        //确诊患者数量
        List<Integer> confirmedDates = new ArrayList<>();
        // 新增患者数量
        List<Integer> increaseDates = new ArrayList<>();
        increaseDates.add(0);
        for(int i = 0; i < deadDates.size(); i++){
            confirmedDates.add(deadDates.get(i) + curedDates.get(i) + mildDates.get(i) + seriousDates.get(i));
            if(i != 0){
                increaseDates.add(confirmedDates.get(i) - confirmedDates.get(i - 1));
            }
        }
        //封装成JSON数据需要的格式
        Calendar s = Calendar.getInstance();
        s.setTime(startDate);
        Calendar e = Calendar.getInstance();
        e.setTime(now);
        //日期比较的单位为天，因为统计数据是以天为单位归档的
        s.set(Calendar.HOUR_OF_DAY, 0);
        s.set(Calendar.MINUTE, 0);s.set(Calendar.SECOND, 0);s.set(Calendar.MILLISECOND, 0);
        e.set(Calendar.HOUR_OF_DAY, 0);
        e.set(Calendar.MINUTE, 0);e.set(Calendar.SECOND, 0);e.set(Calendar.MILLISECOND, 0);
        List<List<Object>> res = new ArrayList<>();
        int i = 0;
        int boundary = curedDates.size();
        //统计的时间范围为：疫情事件发布那天 -> 昨天(今天的数据需到23：59分时统计)
        while(s.compareTo(e) < 0){
            if(i >= boundary) {
                break;
            }
            String date = s.get(Calendar.YEAR) + "-" + (s.get(Calendar.MONTH) + 1) + "-" + s.get(Calendar.DAY_OF_MONTH);
            List<Object> line = Arrays.asList(date, curedDates.get(i), mildDates.get(i), seriousDates.get(i), deadDates.get(i),
                    confirmedDates.get(i) , increaseDates.get(i));
            res.add(line);
            i++;
            s.add(Calendar.DAY_OF_YEAR, 1);
        }
        return res;
    }

}