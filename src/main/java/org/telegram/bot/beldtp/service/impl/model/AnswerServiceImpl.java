package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.repository.interf.AnswerRepository;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;

import java.util.List;

@Service
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public List<Answer> getAll() {
        return answerRepository.getAll();
    }

    @Override
    public List<Answer> get(Language language) {
        return answerRepository.get(language);
    }

    @Override
    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    @Override
    public Answer get(Long id) {
        return answerRepository.get(id);
    }

    @Override
    public List<Answer> get(String type) {
        return answerRepository.get(type);
    }

    @Override
    public Answer get(String type, Language language) {
        return answerRepository.get(type,language);
    }
}
