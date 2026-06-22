package ru.ncfu.autoshow.exception;

/** Запрашиваемый ресурс не найден (HTTP 404). */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String entity, Object id) {
        super(entity + " с идентификатором " + id + " не найден(а)");
    }
}
