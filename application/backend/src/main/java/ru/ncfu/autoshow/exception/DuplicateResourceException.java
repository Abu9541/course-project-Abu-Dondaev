package ru.ncfu.autoshow.exception;

/** Нарушение уникальности (ресурс уже существует, HTTP 409). */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
