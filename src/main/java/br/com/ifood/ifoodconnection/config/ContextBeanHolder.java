package br.com.ifood.ifoodconnection.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class ContextBeanHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    //get any bean in application context
    public static <T> T getBean(Class<T> tClass) {
        Objects.requireNonNull(applicationContext, "Application Context not initialized");
        return applicationContext.getBean(tClass);
    }

}
