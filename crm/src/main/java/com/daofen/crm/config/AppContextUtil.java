package com.daofen.crm.config;

import org.springframework.context.ApplicationContext;

public class AppContextUtil {

    private static ApplicationContext applicationContext = null;

    private AppContextUtil() {
    }

    public static ApplicationContext get() {
        return applicationContext;
    }

    public static void set(ApplicationContext context) {
        applicationContext = context;
    }


    public static Object getBean(String key) {
        return applicationContext.getBean(key);
    }

    public static <T> T getBean(Class<T> c) {

        return applicationContext.getBean(c);
    }

    public static <T> T getBean(String key, Class<T> c) {

        return applicationContext.getBean(key, c);
    }
}
