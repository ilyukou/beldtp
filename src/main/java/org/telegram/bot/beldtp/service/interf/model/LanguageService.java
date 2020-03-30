package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.Language;

import java.util.List;

public interface LanguageService {
    List<Language> getAvailableLanguages();
}
