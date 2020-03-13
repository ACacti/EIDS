package com.shj.eids.config;

import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: MybatisConfig
 * @Description: 此类相当于Mybatis配置文件的作用，与application.yaml中的mybatis下面的配置互补
 * @Author: ShangJin
 * @Create: 2020-03-04 22:03
 **/
@Configuration
public class MybatisConfig {
    @Bean
    public ConfigurationCustomizer configurationCustomizer(){
        return  new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
                //开启驼峰命名
                configuration.setMapUnderscoreToCamelCase(true);
                //开启二级缓存
                configuration.setCacheEnabled(true);
//                configuration.setLogImpl(LoggerFactory.);
            }
        };
    }
}