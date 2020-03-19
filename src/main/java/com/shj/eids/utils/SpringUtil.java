package com.shj.eids.utils;

import com.shj.eids.domain.User;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @ClassName: SpringUtil
 * @Description:
 * @Author: ShangJin
 * @Create: 2020-03-19 10:00
 **/
public class SpringUtil {
    private static ConfigurableApplicationContext context;
    private static DefaultListableBeanFactory beanFactory;

    public static void setContext(ConfigurableApplicationContext context) {
        SpringUtil.context = context;
        beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
    }
    public static <T> void addBean(String name, T bean){
        beanFactory.registerSingleton(name, bean);
    }
    /*
     * 另一种向容器添加bean的方法：
     *          1.先定义一个类
     *          2. 设置注入属性
     *          3. 注册到容器中
     * 下面用User类举例
     */
    public static void addBean(){
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(User.class);
        beanDefinitionBuilder.addPropertyValue("id", 1);
//        beanDefinitionBuilder.addPropertyValue("xxxx", beanFactory.getBean("xxx"));

        //注册到容器中
        beanFactory.registerBeanDefinition("user", beanDefinitionBuilder.getBeanDefinition());
    }
    public static <T> T getBean(String name){
        return (T) beanFactory.getBean(name);
    }
}