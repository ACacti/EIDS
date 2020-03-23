package com.shj.eids.interceptor;

import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.service.EpidemicEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @ClassName: EpidemicEventInterceptor
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-22 11:26
 **/
@Component
public class EpidemicEventInterceptor implements HandlerInterceptor {
    @Autowired
    private EpidemicEventService epidemicEventService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if(session.getAttribute("events") == null){
            List<EpidemicEvent> list = epidemicEventService.getAllEvent();
            session.setAttribute("events", list);
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.debug("添加疫情事件信息.");
        }
        return true;
    }
}