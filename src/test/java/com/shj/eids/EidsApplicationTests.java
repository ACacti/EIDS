package com.shj.eids;

import com.alibaba.fastjson.JSON;
import com.shj.eids.dao.PatientInformationMapper;
import com.shj.eids.domain.PatientInformation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootTest
class EidsApplicationTests {
    @Autowired
    JavaMailSenderImpl mailSender;
    @Autowired
    PatientInformationMapper mapper;
//    @Test
    void complexMail() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        //组装
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);//true: 开启多文件
        mimeMessageHelper.setSubject("复杂邮件测试");
        mimeMessageHelper.setText("<h1>这是一封复杂邮件</h1>", true);//true: 支持html内容
        mimeMessageHelper.setTo("1694634080@qq.com");
        mimeMessageHelper.setFrom("shangjinv6@163.com");
        mailSender.send(message);
    }
//    @Test
    void PatientInformationMapperTest(){
        Map<String, Object> map = new HashMap<>();
        List<String> status = new ArrayList<>();
        status.add("轻微");
        map.put("status", status);
        List<PatientInformation> patientInformation = mapper.getPatientInformation(map);
        System.out.println(patientInformation);
    }
//    @Test
    public void CalendarTest(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年-MM月-dd日 HH:mm:ss");

        System.out.println(format.format(calendar.getTime()));
    }
//    @Test
    public void MapTest(){
        Map<String, Object> map = new HashMap<>();
        map.put("name", "上海");
        map.put("value", 40);
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(map);
        System.out.println(JSON.toJSONString(list));
    }
    @Test
    public void dateTest(){
        Date startTime = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        startTime.setMonth(1);
        Calendar now= Calendar.getInstance();
        Calendar start = Calendar.getInstance();
        start.setTime(startTime);
        List<String> list = new ArrayList<>();
        String value;
        while (start.compareTo(now) <= 0){
            value = start.get(Calendar.YEAR) + "-" + start.get(Calendar.MONTH) + "-" + start.get(Calendar.DAY_OF_MONTH);
            list.add(value);
            start.add(Calendar.DAY_OF_YEAR, 1);
        }
        for(String str : list){
            System.out.println(str);
        }
    }


}
