package org.telegram.bot.beldtp.repository.interf;


import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;

public interface AnswerRepository {
    Answer get(String type);

    Answer get(String type, Language language);

    Answer save(Answer answer);

    void delete(Answer answer);
}
