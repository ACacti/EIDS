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
        String[]provinces = EpidemicInfoService.provinces;
        String[]status = EpidemicInfoService.status;
        Admin admin = new Admin(1, "1694634080@qq.com", "111111", 1);
        EpidemicEvent event = eventMapper.getEpidemicEventsByPublisherId(admin.getId(), null, null).get(0);
        Date startTime = event.getReleaseTime();
        Calendar now = Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(startTime);
        Random random = new Random();
        LocalUtil localUtil = LocalUtil.getInstance();
        Integer count = 0;//累加模拟身份证号
        Map<String, List<String>> cityMap = new HashMap<>();
        while (start.compareTo(now) <= 0) {
            //随机生成60条患者数据，并添加到数据库
            for (int i = 0; i < 60; i++) {
                String name = defaultKaptcha.createText();
                String province = provinces[random.nextInt(provinces.length)];
                List<String> cities = cityMap.get(province);
                if(cities == null){
                    cities = localUtil.getCities("中国", province);
                    cityMap.put(province, cities);
                }

                if(cities.size() == 0) continue;
                String city = cities.get(random.nextInt(cities.size()));
                Date reportingTime = start.getTime();
                String s = status[random.nextInt(status.length)];
                String idNumber = count.toString();
                count++;
                PatientInformation patientInformation = new PatientInformation(null, name, province, city, null, null, reportingTime, event, s, idNumber);
                patientInformationMapper.addPatientInformation(patientInformation);
            }
            //模拟每日归档统计
            everyDayCountService.makeEveryDayCount(start.getTime());
            start.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
}