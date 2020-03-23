package com.shj.eids.controller.admin;

import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.service.AdminService;
import com.shj.eids.service.EpidemicEventService;
import com.shj.eids.service.EpidemicInfoService;
import com.shj.eids.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: AdminCotroller
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-22 14:04
 **/
@Controller
public class AdminCotroller {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private EpidemicInfoService infoService;
    @Autowired
    private EpidemicEventService epidemicEventService;

    @RequestMapping("/admin")
    String redirectTo(){
        return "admin/adminindex";
    }
}