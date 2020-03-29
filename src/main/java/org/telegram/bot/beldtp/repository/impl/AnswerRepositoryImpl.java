package org.telegram.bot.beldtp.repository.impl;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.repository.interf.AnswerRepository;
import org.telegram.bot.beldtp.repository.jpa.AnswerJpaRepository;

@Service
public class AnswerRepositoryImpl implements AnswerRepository {

    @Autowired
    private AnswerJpaRepository answerJpaRepository;

    @Override
    public Answer get(String type) {
        return answerJpaRepository.findByType(type);
    }

    @Override
    public Answer get(String type, Language language) {
        return answerJpaRepository.findByTypeAndLanguage(type, language);
    }

    @Override
    public Answer save(Answer answer) {
        return answerJpaRepository.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        answerJpaRepository.delete(answer);
    }
}