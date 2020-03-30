package org.telegram.bot.beldtp.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.model.Answer;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;

@Component
public abstract class Handler {

    @Autowired
    private AnswerService answerService;

    private UserRole accessRight;
    private String type;

    public Answer getAnswer(Language language) {
        return answerService.get(getType(), language);
    }

    public UserRole getAccessRight() {
        return accessRight;
    }

    public void setAccessRight(UserRole accessRight) {
        this.accessRight = accessRight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
