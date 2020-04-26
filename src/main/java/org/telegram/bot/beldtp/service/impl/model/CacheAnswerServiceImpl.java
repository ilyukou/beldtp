package org.telegram.bot.beldtp.service.impl.model;

import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class CacheAnswerServiceImpl implements AnswerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheAnswerServiceImpl.class);

    private  LoadingCache<String, Answer> loadingCache;

    public void setLoadingCache(LoadingCache<String, Answer> loadingCache) {
        this.loadingCache = loadingCache;
    }

    @Autowired
    @Qualifier("not-cache")
    private AnswerService answerService;

    @Override
    public List<Answer> getAll() {
        return answerService.getAll();
    }

    @Override
    public List<Answer> get(Language language) {
        return answerService.get(language);
    }

    @Override
    public Answer save(Answer answer) {
        return answerService.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        answerService.save(answer);
    }

    @Override
    public Answer get(Long id) {
        return answerService.get(id);
    }

    @Override
    public List<Answer> get(String type) {
        return answerService.get(type);
    }

    @Override
    public Answer get(String type, Language language) {
        LOGGER.trace("Get answer type: " + type + ", language: " + language);
        return getByKey(getKey(type,language));
    }

    @Override
    public String getKey(String type, Language language) {
        return answerService.getKey(type, language);
    }

    @Override
    public Answer getByKey(String key) {
        try {
            return loadingCache.get(key);
        } catch (ExecutionException e) {
            return answerService.getByKey(key);
        }
    }
}
