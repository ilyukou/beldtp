package org.telegram.bot.beldtp.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {

    }

    public BadRequestException(String message) {

    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
