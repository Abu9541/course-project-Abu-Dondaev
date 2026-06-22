# База данных ИС «Автосалон»

СУБД: **PostgreSQL 16**, нормализация до **3НФ**.

| Файл | Назначение |
|------|-----------|
| `schema.sql` | DDL: создание 10 таблиц с PK/FK/UNIQUE/CHECK и индексами |
| `seed.sql` | Демонстрационное наполнение (имена — Северный Кавказ; админ — Дондаев Абу) |
| `er-diagram.puml` | ER-диаграмма (PlantUML) |

## Как применяется

- **В приложении** схему создаёт Hibernate (`spring.jpa.hibernate.ddl-auto=update`)
  по JPA-сущностям, а демо-данные загружает `DataInitializer` при первом старте
  (пароли хешируются BCrypt). Отдельно запускать SQL не требуется.
- **Вручную** (для инспекции/проверки DDL):

```bash
psql -U autoshow -d autoshow -f schema.sql
psql -U autoshow -d autoshow -f seed.sql
```

Подробное проектирование и обоснование 3НФ — в
[`../../docs/04-database/database-design.md`](../../docs/04-database/database-design.md).
