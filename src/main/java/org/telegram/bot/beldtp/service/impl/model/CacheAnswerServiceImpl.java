package org.telegram.bot.beldtp.service.impl.model;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheAnswerServiceImpl implements AnswerService {

    private static final long SCHEDULED_FIXED_RATE = 1000 * 60 * 2; // 2 minute

    private static Map<Language, Map<String, Answer>> answers = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier("not-cache")
    private AnswerService answerService;

    public static Map<Language, Map<String, Answer>> getAnswers() {
        return answers;
    }

    public static void setAnswers(Map<Language, Map<String, Answer>> answers) {
        CacheAnswerServiceImpl.answers = answers;
    }

    @Scheduled(fixedRate = SCHEDULED_FIXED_RATE)
    public void update() {

        List<List<Answer>> list = new ArrayList<>();


        for (Language language : Language.values()) {
            list.add(answerService.get(language));
        }

        if (list.size() != 0) {
            answers = getUpdatedMap(list);
        }
    }

    private Map<Language, Map<String, Answer>> getUpdatedMap(List<List<Answer>> answers) {
        Map<Language, Map<String, Answer>> newMap = new ConcurrentHashMap<>();

        for (List<Answer> answerList : answers) {
            Map<String, Answer> map = new ConcurrentHashMap<>();

            for (Answer answer : answerList) {
                map.put(answer.getType(), answer);
            }

            if (answerList.size() > 0) {
                newMap.put(answerList.get(0).getLanguage(), map);
            }
        }

        return insertOldMap(newMap);
    }

    private Map<Language, Map<String, Answer>> insertOldMap(Map<Language, Map<String, Answer>> newMap) {

        // Language for
        for (Map<String, Answer> maps : answers.values()) {
            for (Answer answer : maps.values()) {
                if (newMap.get(answer.getLanguage()) == null) {

                    // put old value by language
                    newMap.put(answer.getLanguage(), answers.get(answer.getLanguage()));

                } else if (newMap.get(answer.getLanguage()).get(answer.getType()) == null) {
                    // put old value by language and type that isn't exist new map

                    Map<String, Answer> map = newMap.get(answer.getLanguage());
                    map.put(answer.getType(), answers.get(answer.getLanguage()).get(answer.getType()));

                    newMap.put(answer.getLanguage(), map);
                }
            }
        }

        return newMap;
    }

    @Override
    public List<Answer> get(String type) {
        List<Answer> list = new ArrayList<>();

        for (Language language : Language.values()) {
            Answer answer = get(type, language);

            if (answer != null) {
                list.add(answer);
            }
        }

        return list;
    }

    @Override
    public Answer get(String type, Language language) {
        Map<String, Answer> map = answers.get(language);

        if (map == null) {
            return null;
        }

        return map.get(type);
    }

    @Override
    public List<Answer> getAll() {
        List<Answer> list = new ArrayList<>();

        for (Map<String, Answer> map : answers.values()) {
            list.addAll(map.values());
        }

        return list;
    }

    @Override
    public List<Answer> get(Language language) {
        Map<String, Answer> map = answers.get(language);

        if (map == null) {
            return new ArrayList<>();
        }

        if (map.size() == 0) {
            return new ArrayList<>();
        }

        return new ArrayList<>(map.values());
    }

    // NOT CACHED METHODS

    @Override
    public Answer get(Long id) {
        return answerService.get(id);
    }

    @Override
    public Answer save(Answer answer) {
        return answerService.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        answerService.delete(answer);
    }
}
