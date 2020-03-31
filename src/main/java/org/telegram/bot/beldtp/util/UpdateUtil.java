package org.telegram.bot.beldtp.util;

import org.springframework.stereotype.Component;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class UpdateUtil {

    public static Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId().longValue();
        }
        if (update.hasEditedMessage()) {
            return update.getEditedMessage().getFrom().getId().longValue();
        }
        return null; // FIXME
    }

    public static String getLanguageCode(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getLanguageCode();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getLanguageCode();
        }
        if (update.hasEditedMessage()) {
            return update.getEditedMessage().getFrom().getLanguageCode();
        }
        return null; // FIXME
    }

    public static Language getLanguage(Update update) {
        Language[] languages = Language.values();

        for (Language language : languages) {
            if (language.toString().toLowerCase()
                    .equals(update.getMessage().toString().toLowerCase())) {
                return language;
            }
        }
        return Language.BE; // default value
    }

    public static String getText(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getText();
        }

        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }

        return null;
    }

}
