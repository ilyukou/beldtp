package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;

import java.util.List;

public interface AnswerService {

    List<Answer> getAll();

    List<Answer> get(Language language);

    Answer save(Answer answer);

    void delete(Answer answer);

    Answer get(Long id);

    List<Answer> get(String type);

    Answer get(String type, Language language);
}
