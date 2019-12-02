package com.aispeech.segment.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 容器
 * 
 * @author huihua.niu
 *
 */
@Component
public class BeanContainer implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanContainer.class);

    private static ApplicationContext applicationContext;

    public synchronized void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        BeanContainer.applicationContext = applicationContext;
        LOGGER.info(
                "ApplicationContext配置成功,在普通类可以通过调用BeanContainer.getAppContext()获取applicationContext对象.");
    }

    /**
     * 获取applicationContext
     * 
     * @return
     */
    public static synchronized ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    // 通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    // 通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    // 通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

}
