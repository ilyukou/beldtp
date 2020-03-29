package org.telegram.bot.beldtp.model;

public enum Language {
    BE("Беларуская мова"),
    RU("Русский язык"),
    EN("English");

    private String value;

    Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
