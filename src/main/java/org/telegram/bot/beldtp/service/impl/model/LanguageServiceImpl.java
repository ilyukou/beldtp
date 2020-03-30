package org.telegram.bot.beldtp.service.impl.model;

import org.jvnet.hk2.annotations.Service;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.service.interf.model.LanguageService;

import java.util.Arrays;
import java.util.List;

@Service
public class LanguageServiceImpl implements LanguageService {

    @Override
    public List<Language> getAvailableLanguages() {
        return Arrays.asList(Language.values());
    }
}
