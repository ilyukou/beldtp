package org.telegram.bot.beldtp.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.bot.beldtp.service.impl.model.AnswerServiceImpl;
import org.telegram.bot.beldtp.service.impl.model.CacheAnswerServiceImpl;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;

@Configuration
public class AnswerServiceConfig {

    // Default return cache service
    @Bean
    @Primary
    public AnswerService cacheAnswerService() {
        return new CacheAnswerServiceImpl();
    }

    @Bean
    @Qualifier("not-cache")
    public AnswerService answerService() {
        return new AnswerServiceImpl();
    }
}
