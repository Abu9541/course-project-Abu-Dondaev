package ru.ncfu.autoshow.exception;

/** Нарушение бизнес-правила (HTTP 422 Unprocessable Entity). */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
