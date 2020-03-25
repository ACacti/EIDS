package com.shj.eids.controller.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.shj.eids.domain.*;
import com.shj.eids.service.*;
import jdk.internal.vm.compiler.collections.EconomicMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private UserService userService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private EpidemicInfoService epidemicInfoService;
    @Autowired
    private EpidemicEventService epidemicEventService;

    @Autowired
    private AidInformationService aidInformationService;
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

    @PostMapping("/admin/usertable/upgrade/{id}")
    public String upgrade(@PathVariable("id") Integer id,
                          HttpSession session){
        Map<String, Object> res = new HashMap<>();
        try{
            Admin admin = (Admin) session.getAttribute("loginAccount");
            User u = userService.getUserById(id);
            Integer level = u.getLevel();
            if(level >= 2){
                res.put("level", level);
            }
            else{
                adminService.upgradeUser(admin, u);
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
                adminService.downgradeUser(admin, u);
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
                                    @RequestParam("limit") Integer limit){
        HashMap<String, Object> res= new HashMap<>();
        try{
            List<AidInformation> list = aidInformationService.getAidInformation((page - 1) * limit, limit);
            res.put("data", list);
            res.put("code", 0);
            res.put("count",aidInformationService.getCount());
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
    public String deleteAidInformation(@RequestParam("ids") List<String> ids){
        Map<String, Object> res = new HashMap<>();
        try {
            aidInformationService.deleteAidInformation(ids);
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

}