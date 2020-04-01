package com.shj.eids;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baidu.aip.face.AipFace;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.shj.eids.dao.EpidemicEventMapper;
import com.shj.eids.dao.EveryDayCountMapper;
import com.shj.eids.dao.PatientInformationMapper;
import com.shj.eids.domain.EpidemicMsg;
import com.shj.eids.domain.PatientInformation;
import com.shj.eids.service.EpidemicMsgService;
import com.shj.eids.service.EveryDayCountService;
import com.shj.eids.utils.AipFaceUtils;
import com.shj.eids.utils.Base64Util;
import com.shj.eids.utils.EmailUtil;
import com.shj.eids.utils.LocalUtil;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootTest
class EidsApplicationTests {
    @Autowired
    JavaMailSenderImpl mailSender;
    @Autowired
    PatientInformationMapper mapper;
    @Autowired
    EpidemicEventMapper eventMapper;
    @Autowired
    EveryDayCountMapper everyDayCountMapper;
    @Autowired
    PatientInformationMapper patientInformationMapper;
    @Autowired
    EveryDayCountService everyDayCountService;
    @Autowired
    EpidemicMsgService msgService;
    @Autowired
    DefaultKaptcha defaultKaptcha;
    @Autowired
    EmailUtil emailUtil;

//    @Test
    void localUtilTest(){
        LocalUtil util = LocalUtil.getInstance();
        System.out.println(util.getCities("中国", "上海"));
    }

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
    //@Test
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
//    @Test
    public void pathTest(){
        String str = "C:\\Users\\ShangJin\\Desktop\\WorkSpace\\EpidemicInformationDisseminationSystem\\EIDS\\uploadImage\\log\\6\\";
        File file = new File(str);
        file.mkdir();
    }
//    @Test
    public void test1(){
        int page = 1;

        List<EpidemicMsg> list = msgService.getArticlesAbstract((page - 1) * 8, 8);
        for(EpidemicMsg m : list){
            System.out.println(m.getAuthor());
        }
        System.out.println(JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));
    }

//    @Test
//    public void faceApiTest() throws IOException {
//        //人脸注册
//        String path = "C:\\Users\\ShangJin\\Desktop\\1.jpg";
//        File img = new File(path);
//        String base64Code = Base64Util.encode(img);
//        System.out.println(base64Code);
//        HashMap<String, String> options = new HashMap<>();
//        options.put("user_info", "网络图片");
//        AipFace client = AipFaceUtils.getClient();
//
//        JSONObject res = client.addUser(base64Code, "BASE64", "TestFaceRepository", "webImage1", options);
//        System.out.println(res.toString(2));
//        //人脸搜索
//    }

}
