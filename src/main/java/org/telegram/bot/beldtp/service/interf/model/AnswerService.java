package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;

public interface AnswerService {
    Answer save(Answer answer);

    void delete(Answer answer);

    Answer get(Long id);

    Answer get(String type);

    Answer get(String type, Language language);
}
