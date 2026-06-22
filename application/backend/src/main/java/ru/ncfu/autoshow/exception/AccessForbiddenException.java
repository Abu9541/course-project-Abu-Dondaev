package ru.ncfu.autoshow.exception;

/** Доступ к ресурсу запрещён для текущего пользователя (HTTP 403). */
public class AccessForbiddenException extends RuntimeException {

    public AccessForbiddenException(String message) {
        super(message);
    }
}
