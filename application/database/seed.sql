-- =====================================================================
--  ИС «Автосалон» — наполнение БД демонстрационными данными
--  Пароли захешированы BCrypt (cost=10):
--      admin   -> Admin@123
--      manager -> Manager@123
--      client  -> Client@123
--
--  Внимание: при запуске приложения те же данные создаются автоматически
--  Java-инициализатором (DataInitializer), если таблица users пуста.
--  Этот скрипт предназначен для ручного развёртывания/инспекции БД.
-- =====================================================================

-- ---------- Пользователи -------------------------------------------------
INSERT INTO users (id, full_name, email, password_hash, phone, role, loyalty_level, pdn_consent, active) VALUES
 (1, 'Дондаев Абу Умар-Пашаевич', 'dondaevabu126@gmail.com', '$2b$10$JW97nL09cU0abkoi6jP.MeKo752kwF63KhCJmMzIli2/Xe4A3FnSy', '+79280000001', 'ADMIN',   'PLATINUM', TRUE, TRUE),
 (2, 'Аслан Гадаев',              'manager1@autoshow.ru',     '$2b$10$9WRApEf2snx37UqEI0xMkuGl3H.EM3v93mhGIDHLaAy6CMGYjAMo2', '+79280000002', 'MANAGER', 'GOLD',     TRUE, TRUE),
 (3, 'Тимур Кадыров',             'manager2@autoshow.ru',     '$2b$10$9WRApEf2snx37UqEI0xMkuGl3H.EM3v93mhGIDHLaAy6CMGYjAMo2', '+79280000003', 'MANAGER', 'GOLD',     TRUE, TRUE),
 (4, 'Магомед Дудаев',            'client1@example.com',      '$2b$10$6nnkPE5lXNz9qS5DugAFwOcQB7aOQ/JEC.ehjHQ/sK4MudwF17f2C', '+79280000004', 'CLIENT',  'SILVER',   TRUE, TRUE),
 (5, 'Зарема Алханова',           'client2@example.com',      '$2b$10$6nnkPE5lXNz9qS5DugAFwOcQB7aOQ/JEC.ehjHQ/sK4MudwF17f2C', '+79280000005', 'CLIENT',  'STANDARD', TRUE, TRUE),
 (6, 'Рустам Мамаев',             'client3@example.com',      '$2b$10$6nnkPE5lXNz9qS5DugAFwOcQB7aOQ/JEC.ehjHQ/sK4MudwF17f2C', '+79280000006', 'CLIENT',  'STANDARD', TRUE, TRUE);

-- ---------- Марки --------------------------------------------------------
INSERT INTO brands (id, name, country) VALUES
 (1, 'BMW',           'Германия'),
 (2, 'Mercedes-Benz', 'Германия'),
 (3, 'Audi',          'Германия'),
 (4, 'Toyota',        'Япония'),
 (5, 'Kia',           'Южная Корея'),
 (6, 'Hyundai',       'Южная Корея'),
 (7, 'Lada',          'Россия');

-- ---------- Автомобили ---------------------------------------------------
INSERT INTO vehicles (id, brand_id, model, year, vin, price, body_type, engine_type, transmission, drive_type, color, mileage, power_hp, engine_volume, fuel_consumption, equipment_level, description, status) VALUES
 (1, 1, 'X5 xDrive40i',        2024, 'WBAJA9105LBJ70001', 8990000.00, 'SUV',       'PETROL',   'AUTOMATIC', 'AWD', 'Чёрный',        15,  340, 3.0, 9.8,  'M Sport',   'Премиальный кроссовер с рядным 6-цилиндровым двигателем.',           'IN_STOCK'),
 (2, 1, '320i',                2023, 'WBA5R1109MFH10002', 4350000.00, 'SEDAN',     'PETROL',   'AUTOMATIC', 'RWD', 'Белый',         120, 184, 2.0, 6.6,  'Luxury',    'Классический бизнес-седан BMW 3 серии.',                            'IN_STOCK'),
 (3, 2, 'E 200',               2024, 'W1K2130461A100003', 6790000.00, 'SEDAN',     'PETROL',   'AUTOMATIC', 'RWD', 'Серебристый',   10,  197, 2.0, 7.2,  'AMG Line',  'Бизнес-седан Mercedes E-Class с мягким гибридом.',                  'IN_STOCK'),
 (4, 2, 'GLC 300',             2023, 'W1N2539801V100004', 7250000.00, 'CROSSOVER', 'HYBRID',   'AUTOMATIC', 'AWD', 'Синий',         45,  258, 2.0, 8.1,  'Premium',   'Гибридный кроссовер премиум-класса.',                               'IN_STOCK'),
 (5, 3, 'A4 40 TFSI',          2023, 'WAUZZZF40PN100005', 4690000.00, 'SEDAN',     'PETROL',   'ROBOT',     'AWD', 'Серый',         60,  204, 2.0, 6.9,  'S line',    'Седан Audi A4 с полным приводом quattro.',                          'IN_STOCK'),
 (6, 3, 'Q5 45 TFSI',          2024, 'WA1ZZZFY5P2100006', 6390000.00, 'SUV',       'PETROL',   'ROBOT',     'AWD', 'Чёрный',        5,   249, 2.0, 8.4,  'S line',    'Среднеразмерный премиальный кроссовер Audi.',                       'IN_STOCK'),
 (7, 4, 'Camry 2.5',           2024, 'JTNB11HK0P3100007', 3590000.00, 'SEDAN',     'PETROL',   'AUTOMATIC', 'FWD', 'Белый',         8,   200, 2.5, 7.4,  'Prestige',  'Самый популярный бизнес-седан в России.',                           'IN_STOCK'),
 (8, 4, 'RAV4 2.0',            2023, 'JTMB11CA0PD100008', 3290000.00, 'CROSSOVER', 'PETROL',   'CVT',       'AWD', 'Серый',         90,  149, 2.0, 7.0,  'Comfort',   'Надёжный и практичный городской кроссовер.',                        'IN_STOCK'),
 (9, 5, 'K5 2.5',              2023, 'KNAL341CAP5100009', 3190000.00, 'SEDAN',     'PETROL',   'AUTOMATIC', 'FWD', 'Красный',       30,  194, 2.5, 7.8,  'GT-Line',   'Стильный седан Kia K5 в спортивном исполнении.',                    'IN_STOCK'),
 (10,5, 'Sportage 2.0',        2024, 'KNAPH81ABP7100010', 3090000.00, 'CROSSOVER', 'PETROL',   'AUTOMATIC', 'AWD', 'Зелёный',       12,  150, 2.0, 8.0,  'Prestige',  'Современный кроссовер Kia Sportage нового поколения.',              'IN_STOCK'),
 (11,6, 'Sonata 2.5',          2023, 'KMHL341CBPA100011', 2990000.00, 'SEDAN',     'PETROL',   'AUTOMATIC', 'FWD', 'Чёрный',        25,  180, 2.5, 7.6,  'Style',     'Просторный и технологичный седан Hyundai.',                        'IN_STOCK'),
 (12,6, 'Tucson 2.0',          2024, 'KM8JB81ABPU100012', 3290000.00, 'CROSSOVER', 'DIESEL',   'AUTOMATIC', 'AWD', 'Белый',         18,  186, 2.0, 6.2,  'High-Tech', 'Кроссовер Hyundai Tucson с дизельным двигателем.',                  'IN_STOCK'),
 (13,7, 'Vesta SW Cross',      2024, 'XTAGFL110P1100013', 1890000.00, 'WAGON',     'PETROL',   'MANUAL',    'FWD', 'Оранжевый',     5,   106, 1.6, 8.2,  'Enjoy',     'Универсал повышенной проходимости Lada Vesta.',                     'IN_STOCK'),
 (14,7, 'Aura',                2024, 'XTAGAB110P1100014', 2090000.00, 'SEDAN',     'PETROL',   'CVT',       'FWD', 'Серый',         3,   122, 1.8, 7.5,  'Status',    'Удлинённый комфортный седан Lada Aura.',                            'IN_STOCK'),
 (15,1, 'M3 Competition',      2023, 'WBS43AY0XPFL10015', 11500000.00,'SEDAN',     'PETROL',   'AUTOMATIC', 'AWD', 'Синий',         8000,510, 3.0, 10.1, 'Competition','Высокопроизводительный спортивный седан BMW M3.',                  'RESERVED'),
 (16,2, 'S 500 4MATIC',        2022, 'W1K2231761A100016', 13900000.00,'SEDAN',     'HYBRID',   'AUTOMATIC', 'AWD', 'Чёрный',        21000,435,3.0, 8.9,  'First Class','Флагманский представительский седан Mercedes S-Class.',           'SOLD');

-- ---------- Фотогалерея (доп. изображения) -------------------------------
INSERT INTO vehicle_images (vehicle_id, url, sort_order) VALUES
 (1, 'https://images.unsplash.com/photo-1556189250-72ba954cfc2b', 1),
 (1, 'https://images.unsplash.com/photo-1503376780353-7e6692767b70', 2),
 (7, 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb', 1);

-- ---------- Избранное ----------------------------------------------------
INSERT INTO favorites (user_id, vehicle_id) VALUES
 (4, 1), (4, 7), (5, 3), (6, 9);

-- ---------- Записи на тест-драйв ----------------------------------------
INSERT INTO test_drives (user_id, vehicle_id, manager_id, dealer_center, scheduled_at, status, contact_phone, notes) VALUES
 (4, 1, 2, 'Автосалон на Ленина, 100', now() + INTERVAL '2 day',  'CONFIRMED', '+79280000004', 'Интересует версия M Sport'),
 (5, 3, 3, 'Автосалон на Мира, 25',    now() + INTERVAL '3 day',  'PENDING',   '+79280000005', NULL),
 (6, 9, 2, 'Автосалон на Ленина, 100', now() - INTERVAL '5 day',  'COMPLETED', '+79280000006', 'Клиент остался доволен');

-- ---------- Заказы и рассрочка ------------------------------------------
INSERT INTO orders (id, user_id, vehicle_id, manager_id, payment_type, status, total_price) VALUES
 (1, 4, 16, 2, 'FULL',        'COMPLETED', 13900000.00),
 (2, 5, 15, 3, 'INSTALLMENT', 'CONFIRMED', 11500000.00);

INSERT INTO installment_plans (order_id, down_payment, term_months, interest_rate, monthly_payment, total_amount) VALUES
 (2, 3450000.00, 36, 12.50, 269930.00, 13167480.00);

-- ---------- Отзывы -------------------------------------------------------
INSERT INTO reviews (user_id, vehicle_id, rating, comment) VALUES
 (4, 16, 5, 'Превосходный автомобиль, обслуживание на высоте!'),
 (6, 9,  4, 'Хорошая динамика и комплектация за свои деньги.');

-- ---------- Уведомления --------------------------------------------------
INSERT INTO notifications (user_id, title, message, type, is_read) VALUES
 (4, 'Тест-драйв подтверждён', 'Ваша запись на тест-драйв BMW X5 подтверждена менеджером.', 'TEST_DRIVE', FALSE),
 (5, 'Заявка на рассрочку',    'Ваша заявка на покупку BMW M3 в рассрочку принята в обработку.', 'INSTALLMENT', FALSE);

-- ---------- Сброс счётчиков IDENTITY на максимум ------------------------
SELECT setval(pg_get_serial_sequence('users','id'),             (SELECT MAX(id) FROM users));
SELECT setval(pg_get_serial_sequence('brands','id'),            (SELECT MAX(id) FROM brands));
SELECT setval(pg_get_serial_sequence('vehicles','id'),          (SELECT MAX(id) FROM vehicles));
SELECT setval(pg_get_serial_sequence('vehicle_images','id'),    (SELECT MAX(id) FROM vehicle_images));
SELECT setval(pg_get_serial_sequence('test_drives','id'),       (SELECT MAX(id) FROM test_drives));
SELECT setval(pg_get_serial_sequence('orders','id'),            (SELECT MAX(id) FROM orders));
SELECT setval(pg_get_serial_sequence('installment_plans','id'), (SELECT MAX(id) FROM installment_plans));
SELECT setval(pg_get_serial_sequence('favorites','id'),         (SELECT MAX(id) FROM favorites));
SELECT setval(pg_get_serial_sequence('reviews','id'),           (SELECT MAX(id) FROM reviews));
SELECT setval(pg_get_serial_sequence('notifications','id'),     (SELECT MAX(id) FROM notifications));
