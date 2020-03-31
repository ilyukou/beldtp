package org.telegram.bot.beldtp.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;

import java.util.List;

public interface AnswerJpaRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByType(String type);

    Answer findByTypeAndLanguage(String type, Language language);
}
