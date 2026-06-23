# Документация проекта «ИС Автосалон»

Проектная документация курсового проекта по дисциплине «Программная инженерия»
(траектория **В — мобильная разработка**, архитектура **PCMEF**). Документы организованы
по этапам МУ. Диаграммы выполнены на **Mermaid** и отображаются прямо на GitHub.

## Навигация по этапам

### Этап 0. Инициация и бизнес-анализ — [`00-initiation/`](00-initiation/)
- [Паспорт проекта](00-initiation/project-passport.md)
- [Диаграмма бизнес-контекста (IDEF0 A-0)](00-initiation/business-context-idef0.md)
- [Диаграмма бизнес-прецедентов (BUC)](00-initiation/business-use-cases.md)
- [Бизнес-глоссарий](00-initiation/business-glossary.md)
- [Модель бизнес-классов](00-initiation/business-domain-model.md)
- [Матрица стейкхолдеров](00-initiation/stakeholders.md)
- [SWOT-анализ](00-initiation/swot-analysis.md)
- [Экономическое обоснование (ROI) и CJM](00-initiation/economic-justification.md)

### Этап 1. Требования — [`01-requirements/`](01-requirements/)
- [Use Case диаграмма](01-requirements/use-case-diagram.md)
- [Спецификации прецедентов](01-requirements/use-case-specifications.md)
- [Domain Model](01-requirements/domain-model.md)
- [Глоссарий (системный)](01-requirements/glossary.md)
- [Таблица трассировки](01-requirements/traceability-matrix.md)

### Этап 2. Архитектура — [`02-architecture/`](02-architecture/)
- [Описание архитектуры PCMEF](02-architecture/pcmef-architecture.md)
- [Диаграмма пакетов](02-architecture/package-diagram.md)
- [Спецификация интерфейсов слоёв](02-architecture/layer-interfaces.md)
- [Диаграмма зависимостей (ацикличность)](02-architecture/dependency-diagram.md)
- [Архитектурные решения (ADR)](02-architecture/adr.md)

### Этап 3. База данных — [`03-database/`](03-database/)
- [Проектирование БД (DDL, 3НФ)](03-database/database-design.md)
- [ER-диаграмма](03-database/er-diagram.md)
- [Стратегия ORM](03-database/orm-mapping.md)

### Этап 4. Детальное проектирование — [`04-detailed-design/`](04-detailed-design/)
- [Диаграммы последовательности](04-detailed-design/sequence-diagrams.md)
- [Диаграмма классов проектирования](04-detailed-design/design-class-diagram.md)
- [Спецификация методов](04-detailed-design/method-specifications.md)

### Этап 5. Реализация — [`05-implementation/`](05-implementation/)
- [Отчёт о реализации](05-implementation/implementation-report.md)
- [Отчёт о тестировании и покрытии](05-implementation/testing-report.md)

### Этап 6. Рефакторинг и качество — [`06-refactoring/`](06-refactoring/)
- [Статический анализ](06-refactoring/static-analysis.md)
- [Применённые паттерны проектирования](06-refactoring/design-patterns.md)

### Этап 7. Управление и документация — [`07-management/`](07-management/)
- [Иерархическая структура работ (WBS)](07-management/wbs.md)
- [Календарный план (диаграмма Ганта)](07-management/gantt-chart.md)
- [Оценка трудозатрат (COCOMO)](07-management/cocomo-estimation.md)
- [Техническое задание (ТЗ)](07-management/technical-specification.md)
- [Руководство пользователя](07-management/user-guide.md)
- [Руководство администратора](07-management/admin-guide.md)
- Пояснительная записка — добавляется отдельно в формате `.docx`

## Прочее

- [Соответствие требованиям МУ](REQUIREMENTS-COMPLIANCE.md)
- [`images/`](images/) — скриншоты: экраны приложения, Swagger UI, покрытие JaCoCo, статический анализ, статистика Git
- Исходный код (БД, сервер, Android-клиент) — в папке [`../application`](../application)

> Диаграммы (UML/IDEF0/ER/Гант) реализованы на Mermaid и рендерятся GitHub автоматически
> при просмотре `.md`-файлов.
