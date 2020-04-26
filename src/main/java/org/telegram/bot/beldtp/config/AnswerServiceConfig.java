package org.telegram.bot.beldtp.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.repository.interf.AnswerRepository;
import org.telegram.bot.beldtp.service.impl.model.AnswerServiceImpl;
import org.telegram.bot.beldtp.service.impl.model.CacheAnswerServiceImpl;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Configuration
public class AnswerServiceConfig {

    @Autowired
    private AnswerRepository answerRepository;

    private LoadingCache<String, Answer> loadingCache = CacheBuilder
            .newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<String, Answer>() {
                @Override
                public Answer load(String key) throws Exception {
                    return answerService().getByKey(key);
                }
            });

    public Answer getAnswer(String key) throws ExecutionException {
        return loadingCache.get(key);
    }


    // Default return cache service
    @Bean
    @Primary
    public AnswerService cacheAnswerService() {

        CacheAnswerServiceImpl cacheAnswerService = new CacheAnswerServiceImpl();
        cacheAnswerService.setLoadingCache(loadingCache);
        return cacheAnswerService;
    }

    @Bean
    @Qualifier("not-cache")
    public AnswerService answerService() {
        return new AnswerServiceImpl();
    }
}
