package com.shj.eids;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.shj.eids.dao.EpidemicEventMapper;
import com.shj.eids.dao.PatientInformationMapper;
import com.shj.eids.domain.Admin;
import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.domain.PatientInformation;
import com.shj.eids.service.EpidemicInfoService;
import com.shj.eids.service.EveryDayCountService;
import com.shj.eids.utils.LocalUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * @ClassName: GenerateData
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-13 22:21
 **/
@SpringBootTest
public class GenerateData {
    @Autowired
    EpidemicEventMapper eventMapper;
    @Autowired
    PatientInformationMapper patientInformationMapper;
    @Autowired
    EveryDayCountService everyDayCountService;

    @Autowired
    DefaultKaptcha defaultKaptcha;

    static Long count = 37132419970808L;//累加模拟身份证号

    //可供选择的省信息和患者状态信息
    static String[] provinces = EpidemicInfoService.provinces;//34省+ 南海诸岛 一共35个
    static String[] status = EpidemicInfoService.status;
    static LocalUtil localUtil = LocalUtil.getInstance();

    //缓存获取的城市列表
    static Map<String, List<String>> cityMap = new HashMap<>();

    static Random random = new Random();


    /*
     * @Title: virtualTest
     * @Description: 模拟患者数据的添加以及每天的统计存储工作，默认开始时间为疫情事件的发布时间，变量：startTime
     * @param :
     * @return void
     * @Author: ShangJin
     * @Date: 2020/3/13
     */
    @Test
    void generate(){

        Admin admin = new Admin(1, "1694634080@qq.com", "111111", 1);
        EpidemicEvent event = eventMapper.getEpidemicEventsByPublisherId(admin.getId(), null, null).get(0);
        //模拟开始时间为疫情事件的发布时间
        Date startTime = event.getReleaseTime();
        Calendar start = Calendar.getInstance();
        start.setTime(startTime);
        //part1 模拟疫情爆发阶段时间,为疫情事件发布时间到当前时间的20天前
        Calendar part1 = Calendar.getInstance();
        part1.add(Calendar.DAY_OF_YEAR, -20);
        // part2 模拟疫情消退阶段，为当前时间10天前到当前
        Calendar part2 = Calendar.getInstance();

        //爆发阶段模拟
        part1Data(start, part1, 200,event);

        part1.add(Calendar.DAY_OF_YEAR, 1);
//        消退阶段模拟
        part2Data(part1, part2, event, 150, 100);

    }
    //用二次函数模拟类似正态分布的数据
    static int getIndex(int n){
        int mid = (n % 2 == 0 ? n / 2: n / 2 + 1);
        if(mid <= 0){
            return 0;
        }
        int max = mid * mid;
        int t = (int) Math.sqrt(random.nextInt(max + 1) * 1.0);
        if(t != mid && random.nextInt() % 2 == 0){
            t = n - t;
        }
        if(t == 13){
            t = 17;
        }
        return Math.max((t - 1), 0);
    }
    //
    static String getStatus(){
        int t = random.nextInt(2);
        if(t == 0){
            return status[3];// 1/3 轻微
        }
        t = random.nextInt(4);
        if(t == 0){
            return status[2];//死亡 1/6
        }
        else if(t <= 2){
            return status[0];// 危重1/3
        }
        return status[1];// 治愈：1/6
    }


    //模拟向数据库模拟开始时间start 到结束时间 e 每天向数据库插入 n条数据
    void part1Data(Calendar start, Calendar e, int n, EpidemicEvent event) {
        while (start.compareTo(e) <= 0) {
            //每天随机生成n条患者数据，并添加到数据库
            for (int i = 0; i < n; i++) {
                PatientInformation patientInformation = createOneInformation(start, event);
                patientInformationMapper.addPatientInformation(patientInformation);
            }
            //模拟每日归档统计
            everyDayCountService.makeEveryDayCount(start.getTime());
            start.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
    //模拟向数据库从开始时间start到结束时间end, 每天减少当前确诊人数n, 每天增加确诊人数m
    void part2Data(Calendar start, Calendar end, EpidemicEvent event, int n, int m){
        //遍历时间
        while (start.compareTo(end) <= 0){
            Map<String, Object> args = new HashMap<>();
            args.put("status", Arrays.asList("轻微", "危重"));
            args.put("start", 0);
            args.put("length", n);
            //每天减少n个危重/轻微人数
            List<PatientInformation> list = patientInformationMapper.getPatientInformation(args);
            for(PatientInformation info : list){
                int t = random.nextInt(10);
                if(t > 5){
                    info.setStatus("死亡"); //40%
                }else{
                    info.setStatus("治愈");//60%
                }
                patientInformationMapper.updatePatientInformation(info);
            }
            //每天新增m个确诊人数
            for(int i = 0; i < m; i++){
                PatientInformation info = createOneInformation(start, event);
                patientInformationMapper.addPatientInformation(info);
            }
            everyDayCountService.makeEveryDayCount(start.getTime());
            start.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
    PatientInformation createOneInformation(Calendar time, EpidemicEvent event){
        //生成患者姓名
        String name = defaultKaptcha.createText();
        //生成患者所在省
        int t = getIndex(35);
        String province = provinces[t];

        List<String> cities = cityMap.get(province);
        if (cities == null) {
            cities = localUtil.getCities("中国", province);
            cityMap.put(province, cities);
        }
        //生成患者所在城市
        String city = cities.get(getIndex(cities.size() / 2));
        //患者确诊时间
        Date reportingTime = time.getTime();
        //患者状态
        String s = getStatus();
        String idNumber = count.toString();
        count++;
        return new PatientInformation(null, name, province, city, null, null, reportingTime, event, s, count.toString());

    }

//    @Test
    void test1(){
        Map<String, Integer> provinceMap = new HashMap<>();
        for(String province: provinces){
            provinceMap.put(province, 0);
        }
        EpidemicEvent event = eventMapper.getEpidemicEventsByPublisherId(1, null, null).get(0);
        for(int i = 0; i <= 10000; i++){
            PatientInformation info = createOneInformation(Calendar.getInstance(), event);
            Integer t = provinceMap.get(info.getLocationProvince());
            provinceMap.put(info.getLocationProvince(), t + 1);
        }
        for(String province: provinces){
            System.out.println(province + ":" + provinceMap.get(province));
        }
    }
}