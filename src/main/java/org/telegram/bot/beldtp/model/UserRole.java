package org.telegram.bot.beldtp.model;

public enum UserRole {
    USER(0),
    MODERATOR(10),
    ADMIN(100);

    private int value;

    UserRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
