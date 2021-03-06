package org.telegram.bot.beldtp.handler.subclasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.annotation.HandlerInfo;
import org.telegram.bot.beldtp.exception.BadRequestException;
import org.telegram.bot.beldtp.handler.Handler;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.TelegramResponse;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.service.interf.model.AnswerService;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@HandlerInfo(type = "language", accessRight = UserRole.USER)
public class LanguageHandler extends Handler {

    @Autowired
    private UserService userService;

    @Autowired
    private AnswerService answerService;

    @Override
    public InlineKeyboardMarkup getInlineKeyboardMarkup(User user, Update update) {
        Language[] languages = Language.values();

        return new InlineKeyboardMarkup().setKeyboard(Arrays.stream(languages)
                .map(language -> Collections.singletonList(new InlineKeyboardButton()
                        .setCallbackData(language.toString())
                        .setText(language.getValue())))
                .collect(Collectors.toList()));
    }


    @Override
    public List<TelegramResponse> handle(User user, Update update) {

        Language[] languages = Language.values();

        Language newLanguage = null;

        for (Language language : languages) {
            if (language.toString().equals(update.getCallbackQuery().getData())) {
                newLanguage = language;
                break;
            }
        }

        if (newLanguage == null) {
            throw new BadRequestException();
        }

        user.setLanguage(newLanguage);

        if (user.peekStatus().equals(getType())) {
            user.popStatus();
        }

        user = userService.save(user);

        return getHandlerByStatus(user.peekStatus()).getMessage(user, update);
    }
}
