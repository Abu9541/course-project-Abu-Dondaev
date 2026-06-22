package ru.ncfu.autoshow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа серверной части ИС «Автосалон».
 *
 * <p>Архитектура сервера соответствует паттерну PCMEF:
 * <ul>
 *     <li>{@code control}    — Control: REST-контроллеры (обработка запросов);</li>
 *     <li>{@code mediator}   — Mediator: сервисы (бизнес-логика, транзакции);</li>
 *     <li>{@code entity}     — Entity:   JPA-сущности (состояние, бизнес-методы);</li>
 *     <li>{@code foundation} — Foundation: репозитории (доступ к данным).</li>
 * </ul>
 * Зависимости направлены строго вниз: Control → Mediator → Foundation, Entity.
 */
@SpringBootApplication
public class AutoshowApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoshowApplication.class, args);
    }
}
