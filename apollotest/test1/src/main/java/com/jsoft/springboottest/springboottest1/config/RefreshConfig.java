package com.jsoft.springboottest.springboottest1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;

/**
 * @author chenzq@yunrong.cn
 * @version V2.1
 * @since 2.1.0 2019-05-27 14:22
 */

@Component
public class RefreshConfig implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(RefreshConfig.class);

    ApplicationContext applicationContext;

    @Autowired
    RefreshScope refreshScope;

    @ApolloConfigChangeListener(ConfigConsts.NAMESPACE_APPLICATION)
    public void onChange(ConfigChangeEvent changeEvent) {
        boolean eurekaPropertiesChanged = false;
        for (String changedKey : changeEvent.changedKeys()) {
            log.info("===============================================================");
            log.info("changedKey:{} value:{}", changedKey, changeEvent.getChange(changedKey));
            ConfigChange configChange = changeEvent.getChange(changedKey);
            configChange.getOldValue();
            eurekaPropertiesChanged = true;
            break;
        }
        refreshProperties(changeEvent);
        if (eurekaPropertiesChanged) {
            refreshProperties(changeEvent);
        }
    }

    public void refreshProperties(ConfigChangeEvent changeEvent) {
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        refreshScope.refreshAll();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}