package org.telegram.bot.beldtp.annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.handler.HandlerMap;

@Component
public class HandlerInfoAnnotationBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerInfoAnnotationBeanPostProcessor.class);
    private static final byte MIN_BUTTON_IN_ROW = 1;
    private static final byte MAX_BUTTON_IN_ROW = 6;
    private static final byte DEFAULT_BUTTON_IN_ROW = 1;

    @Autowired
    private HandlerMap handlerMap;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> managedBeanClass = bean.getClass();
        HandlerInfo mapper = managedBeanClass.getAnnotation(HandlerInfo.class);

        if (mapper == null) {
            return bean;
        }

        if(!(bean instanceof Handler)){
            LOGGER.error(beanName + " have annotation " + HandlerInfo.class.getName()
                    + " but don't instance of " + Handler.class.getName());

            throw new BeanNotOfRequiredTypeException(beanName, Handler.class, bean.getClass());
        }

        Handler handler = (Handler) bean;

        if (handlerMap.isExist(handler.getType())) {
            String e = Handler.class.getName() + " with such type are exist. " +
                    "Type: " + handler.getType() + " . Class with such key is "
                    + handlerMap.get(handler.getType()).getClass().getName();

            LOGGER.error(e);
            throw new IllegalArgumentException(e);
        }

        if (mapper.maxButtonInRow() < MIN_BUTTON_IN_ROW || mapper.maxButtonInRow() > MAX_BUTTON_IN_ROW) {
            handler.setMaxButtonInRow(DEFAULT_BUTTON_IN_ROW);
        } else {
            handler.setMaxButtonInRow(mapper.maxButtonInRow());
        }

        handler.setType(mapper.type());
        handler.setAccessRight(mapper.accessRight());

        handlerMap.put(handler);

        return handler;


    }
}
