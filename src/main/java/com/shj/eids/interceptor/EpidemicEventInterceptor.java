package com.shj.eids.interceptor;

import com.shj.eids.domain.EpidemicEvent;
import com.shj.eids.service.EpidemicEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        ServletContext context = request.getServletContext();
        List<EpidemicEvent> events = (List<EpidemicEvent>) context.getAttribute("events");
        if(events == null){
            events = epidemicEventService.getAllEvent();
            context.setAttribute("events", events);
        }
        return true;
    }
}