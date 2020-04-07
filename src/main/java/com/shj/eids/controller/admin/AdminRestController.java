package com.shj.eids.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.shj.eids.dao.RecordAdminAidinfoMapper;
import com.shj.eids.domain.*;
import com.shj.eids.service.*;
import com.shj.eids.utils.*;
import jdk.internal.vm.compiler.collections.EconomicMap;
import org.apache.ibatis.annotations.Param;
import org.json.JSONObject;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @ClassName: AdminRestController
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-23 14:16
 **/
@RestController
public class AdminRestController {
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private EpidemicInfoService epidemicInfoService;
    @Autowired
    private EpidemicEventService epidemicEventService;
    @Autowired
    private AidInformationService aidInformationService;
    @Autowired
    private EpidemicMsgService epidemicMsgService;

    @Autowired
    private DefaultKaptcha defaultKaptcha;
    @Autowired
    private PatientInformationService patientInformationService;
    /*
     * 处理用户管理界面用户表分页显示的请求
     */
    @PostMapping("/admin/usertable/users")
    public String getUsers(@RequestParam(name="email", required = false) String email,
                           @RequestParam("limit") Integer limit,
                           @RequestParam("page") Integer page){
        Integer start = limit * (page - 1);
        email = "".equals(email) ? null:email;
        Map<String, Object> res = new HashMap<>();
        try {
            List<User> list = userService.getUsers(null, email, null, null, true, start, limit);
            res.put("code", 0);
            res.put("count", userService.getCount(null, email, null, null, true, start, limit));
            res.put("data", list);
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code", 1);
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/usertable/upgrade")
    public String upgrade(HttpServletRequest request,
                          HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try{
            Integer id = Integer.parseInt(request.getParameter("id"));
            Admin admin = (Admin) session.getAttribute("loginAccount");
            User u = userService.getUserById(id);
            Integer level = u.getLevel();
            if(level >= 2){
                //目前设计用户等级最高为2，超过2不予升级
                res.put("level", level);
            }
            else{
                String introduction = request.getParameter("introduction");
                userService.upgradeUser(admin, u, introduction);
                res.put("level", level + 1);
            }
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/usertable/downgrade/{id}")
    public String downgrade(@PathVariable("id") Integer id,
                            HttpSession session){
        Admin admin = (Admin) session.getAttribute("loginAccount");
        Map<String, Object> res = new HashMap<>();
        try{
            User u = userService.getUserById(id);
            Integer level = u.getLevel();
            if(level <= 1){
                res.put("level", level);
            }
            else{
                userService.downgradeUser(admin, u);
                res.put("level", level - 1);
            }
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    /*
     * 返回当前登录的管理员的对用户的操作记录
     */
    @PostMapping("/admin/usertable/records")
    public String getUserRecord(@RequestParam(name="email", required = false) String email,
                           @RequestParam("limit") Integer limit,
                           @RequestParam("page") Integer page,
                                HttpSession seesion){
        Map<String, Object> res = new HashMap<>();
        Integer start = limit * (page - 1);
        try {
            Admin admin = (Admin) seesion.getAttribute("loginAccount");
            List<RecordAdminUser> record = null;

            //判断前端传来的email参数
            if(email != null && !"".equals(email)){
//                email不为空，表示查询管理员对相应用户的操作记录
                User user = userService.getUserByEmail(email);
                //判断数据库获取的email对应的user类：
                if(user != null){
                    // 若user不为空，则返回管理员对该用户的操作记录
                    record = adminService.getRecords(admin, user, start, limit);
                    res.put("count", adminService.getAdminUserRecordCount(admin, user));
                }else{
                    // 若user为空，则返回的操作记录也为空，
                    res.put("count", 0);
                }
            }else{
//                email为空，表示查询的操作记录没有用户邮箱的限制
                record = adminService.getRecords(admin, null, start, limit);
                res.put("count", adminService.getAdminUserRecordCount(admin, null));
            }
            res.put("code", 0);
            res.put("data", record);
            return JSON.toJSONString(res,SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code", 1);
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }


    @PostMapping("/admin/eventtable/event")
    public String getEvents(@RequestParam("page") Integer page,
                            @RequestParam("limit") Integer limit){
        Map<String, Object> res = new HashMap<>();
        try{
            List<EpidemicEvent> list = epidemicEventService.getEvents(null, null, null, (page-1) * limit, limit);
            res.put("data", list);
            res.put("code", 0);
            res.put("count", epidemicEventService.getAllCount());
            List<Integer> supplement = new ArrayList<>();
            for(EpidemicEvent event : list){
                supplement.add(epidemicInfoService.getPatientCount(event.getId(), null, null, null, null, null));
            }
            res.put("supplement", supplement);
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code", 1);
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    private void updateServletContextEvents(ServletContext context){
        List<EpidemicEvent> events = epidemicEventService.getAllEvent();
        context.setAttribute("events", events);
    }

    @GetMapping("/admin/eventtable/delete/{id}")
    public String deleteEvent(@PathVariable("id") Integer id,
                              HttpServletRequest request){
        Map<String, Object> res = new HashMap<>();
        try{
            epidemicEventService.deleteEpidemicEventById(id);
            updateServletContextEvents(request.getServletContext());
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/eventtable/edit")
    public String editEvent(@RequestParam("eventName") String eventName,
                            @RequestParam("newName") String name,
                            HttpServletRequest request){
        Map<String, Object> res= new HashMap<>();
        try{
            EpidemicEvent event = epidemicEventService.getEpidemicEventByName(eventName);
            event.setName(name);
            epidemicEventService.updateEpidemicEvent(event);
            //更新ServletContext域的events值
            updateServletContextEvents(request.getServletContext());
            res.put("msg", "success");
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/eventtable/addEvent")
    public String addEvent(@RequestParam("name") String name,
                           HttpSession session,
                           HttpServletRequest request){
        Map<String, Object> res = new HashMap<>();
        try {
            Admin admin = (Admin) session.getAttribute("loginAccount");
            epidemicEventService.addEpidemicEvent(admin, name);
            //更新ServletContext域的events值
            updateServletContextEvents(request.getServletContext());
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/helptable/help")
    public String getAidInformation(@RequestParam("page") Integer page,
                                    @RequestParam("limit") Integer limit,
                                    @RequestParam(value = "content", required = false) String content){
        HashMap<String, Object> res= new HashMap<>();
        try{
            List<AidInformation> list = aidInformationService.getAidInformation(content,(page - 1) * limit, limit);
            res.put("data", list);
            res.put("code", 0);
            res.put("count",aidInformationService.getCount(content));
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code", 1);
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/helptable/upgrade")
    public String upgradeAidInformation(@RequestParam("id") String id,
                                        HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try{
            Admin admin = (Admin) session.getAttribute("loginAccount");
            Integer weight = aidInformationService.updateAidInformation(id, admin, AidInformationService.UPGRADE);
            res.put("data", weight);
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/helptable/downgrade")
    public String downgradeAidInformation(@RequestParam("id") String id,
                                        HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try{
            Admin admin = (Admin) session.getAttribute("loginAccount");
            Integer weight = aidInformationService.updateAidInformation(id, admin, AidInformationService.DOWNGRADE);
            res.put("data", weight);
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/helptable/delete")
    public String deleteAidInformation(@RequestParam("ids") List<String> ids,
                                       HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try {
            Admin admin = (Admin) session.getAttribute("loginAccount");
            aidInformationService.deleteAidInformation(ids, admin);
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @RequestMapping("/admin/helptable/posthelp")
    public String postAid(@RequestParam("title") String title,
                          @RequestParam("article") String content,
                          HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try {
            Admin admin = (Admin) session.getAttribute("loginAccount");
            aidInformationService.postAidInformation(new AidInformation(UUID.randomUUID().toString().replace("-", ""), title, content, new Date(), 1, admin));
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @RequestMapping("/admin/helptable/record")
    public String getRecordAdminAidInformation(@RequestParam("onlyMine") boolean onlyMine,
                                               @RequestParam("page") Integer page,
                                               @RequestParam("limit") Integer limit,
                                               HttpSession seesion){
        Map<String, Object> res = new HashMap<>();
        List<RecordAdminAidinfo> data;
        Integer count;
        try {
            if(onlyMine){
                Admin admin = (Admin) seesion.getAttribute("loginAccount");
                data = aidInformationService.getAdminAidinformationRecord(admin.getId(), (page - 1) * limit, limit);
                count = aidInformationService.getRecordCount(admin.getId());
            }else {
                data = aidInformationService.getAdminAidinformationRecord(null,(page - 1) * limit, limit);
                count = aidInformationService.getRecordCount(null);
            }
            res.put("code", 0);
            res.put("count", count);
            res.put("data", data);
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code", 1);
            return JSON.toJSONString(res);
        }
    }

    @RequestMapping("/admin/articletable/article")
    public String getArticle(@RequestParam(value = "content", required = false) String content,
                             @RequestParam("page") Integer page,
                             @RequestParam("limit") Integer limit){
        Map<String, Object> res = new HashMap<>();
        try {
            List<EpidemicMsg> data = epidemicMsgService.getArticles(null, content, null, null, (page - 1) * limit, limit,0);
            res.put("data", data);
            res.put("code", 0);
            res.put("count", epidemicMsgService.getCount(content,null, null));
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code", 1);
            return JSON.toJSONString(res);
        }
    }

    @RequestMapping("/admin/articletable/update")
    public String getArticle(@RequestParam("ids") List<Integer> ids,
                             @RequestParam("order") String order,
                             HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try {
            EpidemicMsg epidemicMsg = epidemicMsgService.getArticleById(ids.get(0));
            Admin admin = (Admin) session.getAttribute("loginAccount");
            Integer data = -1;
            switch (order){
                case "upgrade":
                    data = epidemicMsgService.updateEpidemicMsg(epidemicMsg, admin, EpidemicMsgService.UPGRADE);
                    break;
                case "downgrade":
                    data = epidemicMsgService.updateEpidemicMsg(epidemicMsg, admin, EpidemicMsgService.DOWNGRADE);
                    break;
                case "delete":
                    for(Integer id: ids){
                        epidemicMsgService.deleteEpidemicMsg(id, admin);
                    }
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            res.put("data", data);
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @RequestMapping("/admin/articletable/record")
    public String getArticleRecord(@RequestParam("page") Integer page,
                             @RequestParam("limit") Integer limit,
                             @RequestParam("onlyMine") Boolean onlyMine,
                             HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try {
            List<RecordAdminEpidemicMsg> data;
            if(onlyMine){
                Admin admin = (Admin) session.getAttribute("loginAccount");
                data = epidemicMsgService.getRecord(admin.getId(), (page- 1) * limit, limit);
                res.put("count", epidemicMsgService.getRecordCount(admin.getId()));
            }else{
                data = epidemicMsgService.getRecord(null, (page - 1) * limit, limit);
                res.put("count", epidemicMsgService.getRecordCount(null));
            }
            res.put("data", data);
            res.put("code", 0);
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/patienttable/patientinfo")
    public String getPatientInfo(@RequestParam("eventId") Integer eventId,
                                 @RequestParam(value = "province", required = false, defaultValue = "")String province,
                                 @RequestParam(value = "city", required = false, defaultValue = "") String city,
                                 @RequestParam(value = "idNumber", required = false, defaultValue = "") String idNumber,
                                 @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                 @RequestParam("page") Integer page,
                                 @RequestParam("limit") Integer limit){
        Map<String, Object> res = new HashMap<>();
        try {
            province = province.length()==0? null: province;
            city = city.length()==0? null: city;
            idNumber = idNumber.length()==0? null: idNumber;
            name = name.length() == 0 ? null:name;
            List<PatientInformation> patienInfo = patientInformationService.getPatientInformation(province, city, null, null, eventId, (page - 1) * limit, limit,name, idNumber);
            for(PatientInformation info : patienInfo){
                String temp = info.getIdNumber();
                temp = temp.substring(0, temp.length() - 4) + "****";
                info.setIdNumber(temp);
            }
            res.put("data", patienInfo);
            res.put("code", 0);
            res.put("count", patientInformationService.getPatientInformationCount(province, city, null, null, eventId, null, null, idNumber));
            System.out.println("------------------返回参数：" + res.get("data") + "-" + res.get("count"));
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code",1);
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/patienttable/update")
    public String updatePatientInformation(@RequestParam("order") String order,
                                           @RequestParam(value = "ids", required = false) List<Integer> ids,
                                           HttpServletRequest request){
        Map<String, Object> res = new HashMap<>();
        try {
            switch (order){
                case "delete":
                    patientInformationService.deletePatientInformationById(ids);
                    break;
                case "edit":
                    Integer id = Integer.parseInt(request.getParameter("id"));
                    String name = request.getParameter("name");
                    String city = request.getParameter("city");
                    String province = request.getParameter("province");
                    String detail = request.getParameter("detail");
                    String status = request.getParameter("status");
                    PatientInformation info = patientInformationService.getPatientInformationById(id);
                    info.setName(name);
                    info.setLocationProvince(province);
                    info.setLocationCity(city);
                    info.setLocationDetail(detail);
                    info.setStatus(status);
                    patientInformationService.updatePatientInformation(info);
                    break;
                default:
                    throw new IllegalAccessException();
            }
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/patienttable/registerFace")
    public String registerFace(@RequestParam("img") MultipartFile img,
                               @RequestParam("id") Integer patientId,
                               @RequestParam("eventId") Integer eventId,
                               HttpServletRequest request){
        Map<String , Object> res= new HashMap<>();
        try {
            JSONObject jsonObject = patientInformationService.detectFace(img.getInputStream());
            String msg = AipFaceUtils.check(jsonObject);
            if(msg != null){
                //照片面部信息不合格
                res.put("msg",msg);
                res.put("code", 2);
                return JSON.toJSONString(res);
            }
            //照片通过检测，注册人脸
            patientInformationService.registerFace(img.getInputStream(), patientId,eventId);
            //将图片保存到本地
            String realPath = getClass().getResource("/").getPath();
            String url = TransferImageUtil.transferImage(img, realPath, "uploadImage/userface/", request.getContextPath(), patientId.toString());
            //更新病人数据
            PatientInformation info = patientInformationService.getPatientInformationById(patientId);
            info.setFaceUrl(url);
            patientInformationService.updatePatientInformation(info);
            res.put("code", 0);
            return JSON.toJSONString(res);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("code", 1);
            return JSON.toJSONString(res);
        }
    }

    @RequestMapping("/admin/patienttable/getExcelTemplet")
    public void getExcelTemplet(HttpServletRequest request, HttpServletResponse response){
        String realPath = getClass().getResource("/public/demo.xlsx").getPath();
        File file = new File(realPath);
        FileDownloadUtil.doDownload(request, response, file);
    }

    @RequestMapping("/admin/patienttable/import")
    public String importPatientInformation(@RequestParam("file") MultipartFile file,
                                         HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try {
            Admin admin = (Admin) session.getAttribute("loginAccount");
            //读取Excel文件信息，导入导入数据库
            EasyExcel.read(file.getInputStream(), PatientInformationExcelModel.class, new PatientInformationExcelListener(admin, patientInformationService)).sheet().doRead();
            res.put("code", 0);
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code", 1);
            return JSON.toJSONString(res);
        }

    }

    @PostMapping("/admin/adminManagement/admintable/admin")
    public String getAdmins(@RequestParam("page") Integer page,
                            @RequestParam("limit") Integer limit){
        Map<String, Object> res = new HashMap<>();
        try {
            List<Admin> adminList = adminService.getAdmins((page - 1) * limit, limit, 1);
            res.put("data", adminList);
            res.put("code", 0);
            res.put("count", adminService.getAdminCount(1));
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code", 1);
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/adminManagement/adminTable/modifyPassword")
    public String modifyPassword(@RequestParam("id") Integer id,
                                 @RequestParam("newPass") String newPass,
                                 HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try {
            Admin admin = (Admin) session.getAttribute("loginAccount");
            if (admin.getLevel() < 2){
                res.put("msg", "权限不足");
                return JSON.toJSONString(res);
            }
            adminService.modifyPassword(admin, id, newPass);
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/adminManagement/adminTable/delete")
    public String deleteAdmin(@RequestParam("ids") Integer []ids,
                              HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try{
            Admin admin = (Admin) session.getAttribute("loginAccount");
            if(admin.getLevel() < 2){
                res.put("msg", "权限不足");
                return JSON.toJSONString(res);
            }
            adminService.deleteAdmin(ids);
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/adminManagement/adminTable/sendCaptcha")
    public String sendCaptcha(@RequestParam("email") String email,
                              HttpSession session){
        HashMap<String, Object> res = new HashMap<>();
        try {
            String captcha = adminService.sendRegisterCaptcha(email);
            res.put("msg", "success");
            session.setAttribute("captcha", captcha);
            return JSON.toJSONString(res);
        } catch (Exception e) {
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/adminManagement/adminTable/verifyCaptcha")
    public String verifyCaptcha(@RequestParam("captcha") String rawCaptcha,
                                HttpSession session){
        Map<String, Object> res = new HashMap<>();
        String realCaptcha = (String) session.getAttribute("captcha");
        try{
            if(realCaptcha != null && realCaptcha.equals(rawCaptcha)){
                res.put("msg", "success");
            }else{
                res.put("msg", "false");
            }
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/adminManagement/adminTable/addAdmin")
    public String addAdmin(@RequestParam("email") String email,
                           @RequestParam("password") String password,
                           HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try{
            Admin admin = (Admin)session.getAttribute("loginAccount");
            adminService.registerAdmin(email, password, admin);
            res.put("msg", "success");
            return JSON.toJSONString(res);
        }catch (Exception e){
            e.printStackTrace();
            res.put("msg", "error");
            return JSON.toJSONString(res);
        }
    }

    @PostMapping("/admin/adminManagement/adminTable/record")
    public String getAdminManagementRecord(@RequestParam(value = "managedEmail", required = false) String managedEmail,
                                           @RequestParam("page") Integer page,
                                           @RequestParam("limit") Integer limit){
        Map<String, Object> res = new HashMap<>();
        try{
            List<RecordAdminManagement> data;
            int count;
            if(managedEmail == null){
                data = adminService.getAllAdminManagementRecord((page - 1) * limit, limit);
                count = adminService.getAdminManagementRecordCount();
            }else{
                data = adminService.getAdminManagementRecordByManagedEmail(managedEmail, (page - 1) * limit, limit);
                count = adminService.getAdminManagementRecordCountByManagedEmail(managedEmail);
            }
            res.put("data", data);
            res.put("code", 0);
            res.put("count", count);
            return JSON.toJSONString(res, SerializerFeature.DisableCircularReferenceDetect);
        }catch (Exception e){
            e.printStackTrace();
            res.put("code", 1);
            return JSON.toJSONString(res);
        }
    }


}