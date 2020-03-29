package org.telegram.bot.beldtp.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;

public interface AnswerJpaRepository extends JpaRepository<Answer, Long> {
    Answer findByType(String type);

    Answer findByTypeAndLanguage(String type, Language language);
}
