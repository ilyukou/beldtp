package org.telegram.bot.beldtp;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.telegram.bot.beldtp.service.impl.model.AnswerServiceImpl;
import org.telegram.bot.beldtp.service.impl.model.CacheAnswerServiceImpl;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;

@TestConfiguration
public class TestConfig {

    @Bean
    public AnswerService cache(){
//        return new CacheAnswerServiceImpl();
        return null;
    }

    @Bean
    @Qualifier("not-cache")
    public AnswerService answerService(){
//        return Mockito.mock(AnswerService.class);
        return null;
    }
}
