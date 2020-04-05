package org.telegram.bot.beldtp.annotation;


import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link HandlerInfo} access for usage only for {@link org.telegram.bot.beldtp.handler.Handler}
 * If you use {@link HandlerInfo} with another class, than
 * {@link HandlerInfoBeanPostProcessor#postProcessAfterInitialization(Object, String)} throws exception
 *
 * @exception  BeanNotOfRequiredTypeException if class not cast to {@link org.telegram.bot.beldtp.handler.Handler}
 * @exception  IllegalArgumentException if {@link Handler} with such type exist
 */

@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerInfo {

    /**
     * Unique key for {@link org.telegram.bot.beldtp.handler.Handler} like hash.
     * {@link HandlerInfoBeanPostProcessor} set this param in {@link Handler#setType(String)}
     */
    String type();

    /**
     * Access right for {@link org.telegram.bot.beldtp.handler.Handler}
     * If {@link User} have {@link UserRole#getValue()} equal or more than in {@link org.telegram.bot.beldtp.handler.Handler}
     * User have access for handle his message in {@link org.telegram.bot.beldtp.handler.Handler}
     */
    UserRole accessRight();

    /**
     * Required a count of handler in row. Range of accountable start from 1 to 5.
     * If value more than or less than 1-5 {@link HandlerInfoBeanPostProcessor} set 1
     * @return count of button in a row
     */
    byte maxHandlerInRow() default 1;
}
