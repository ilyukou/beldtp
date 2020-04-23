package org.telegram.bot.beldtp.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.repository.interf.AnswerRepository;
import org.telegram.bot.beldtp.repository.jpa.AnswerJpaRepository;

import java.util.List;

@Repository
public class AnswerRepositoryImpl implements AnswerRepository {

    @Autowired
    private AnswerJpaRepository answerJpaRepository;

    @Override
    public List<Answer> get(String type) {
        return answerJpaRepository.findByType(type);
    }

    @Override
    public Answer get(String type, Language language) {
        return answerJpaRepository.findByTypeAndLanguage(type, language);
    }

    @Override
    public Answer save(Answer answer) {
        if (answer.getType() != null && answer.getLanguage() != null) {
            Answer found = get(answer.getType(), answer.getLanguage());

            if (found != null) {
                answer.setId(found.getId());
                return answerJpaRepository.save(answer);
            }
        }
        return answerJpaRepository.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        answerJpaRepository.delete(answer);
    }

    @Override
    public Answer get(Long id) {
        return answerJpaRepository.findById(id).orElse(null);
    }

    @Override
    public List<Answer> getAll() {
        return answerJpaRepository.findAll();
    }

    @Override
    public List<Answer> get(Language language) {
        return answerJpaRepository.findByLanguage(language);
    }
}
