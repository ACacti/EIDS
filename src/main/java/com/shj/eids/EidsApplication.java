package com.shj.eids;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement//开启自动配置
@PropertySource("classpath:db.properties")//数据库配置文件
@EnableScheduling//每天统计各个疫情时间，各省，各个状态的统计数据
public class EidsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EidsApplication.class, args);
//        ConfigurableApplicationContext context = SpringApplication.run(EidsApplication.class, args);
//        SpringUtil.setContext(context);
    }

}
