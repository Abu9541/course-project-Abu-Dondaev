package ru.ncfu.autoshow.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.entity.*;
import ru.ncfu.autoshow.entity.enums.*;
import ru.ncfu.autoshow.foundation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Наполнение БД демонстрационными данными при первом запуске (если таблица
 * пользователей пуста). Пароли хешируются BCrypt через {@link PasswordEncoder}.
 *
 * Учётные данные по умолчанию:
 *   ADMIN   — dondaevabu126@gmail.com / Admin@123
 *   MANAGER — manager1@autoshow.ru   / Manager@123
 *   CLIENT  — client1@example.com    / Client@123
 */
@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final VehicleRepository vehicleRepository;
    private final TestDriveRepository testDriveRepository;
    private final OrderRepository orderRepository;
    private final FavoriteRepository favoriteRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, BrandRepository brandRepository,
                           VehicleRepository vehicleRepository, TestDriveRepository testDriveRepository,
                           OrderRepository orderRepository, FavoriteRepository favoriteRepository,
                           ReviewRepository reviewRepository, NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.brandRepository = brandRepository;
        this.vehicleRepository = vehicleRepository;
        this.testDriveRepository = testDriveRepository;
        this.orderRepository = orderRepository;
        this.favoriteRepository = favoriteRepository;
        this.reviewRepository = reviewRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("База данных уже содержит данные — инициализация пропущена.");
            return;
        }
        log.info("Инициализация демонстрационных данных...");

        // -------------------- Пользователи --------------------
        User admin = user("Дондаев Абу Умар-Пашаевич", "dondaevabu126@gmail.com", "Admin@123",
                "+79280000001", Role.ADMIN, LoyaltyLevel.PLATINUM);
        User manager1 = user("Аслан Гадаев", "manager1@autoshow.ru", "Manager@123",
                "+79280000002", Role.MANAGER, LoyaltyLevel.GOLD);
        User manager2 = user("Тимур Кадыров", "manager2@autoshow.ru", "Manager@123",
                "+79280000003", Role.MANAGER, LoyaltyLevel.GOLD);
        User client1 = user("Магомед Дудаев", "client1@example.com", "Client@123",
                "+79280000004", Role.CLIENT, LoyaltyLevel.SILVER);
        User client2 = user("Зарема Алханова", "client2@example.com", "Client@123",
                "+79280000005", Role.CLIENT, LoyaltyLevel.STANDARD);
        User client3 = user("Рустам Мамаев", "client3@example.com", "Client@123",
                "+79280000006", Role.CLIENT, LoyaltyLevel.STANDARD);
        userRepository.save(admin);
        userRepository.save(manager1);
        userRepository.save(manager2);
        userRepository.save(client1);
        userRepository.save(client2);
        userRepository.save(client3);

        // -------------------- Марки --------------------
        Brand bmw = brand("BMW", "Германия");
        Brand mb = brand("Mercedes-Benz", "Германия");
        Brand audi = brand("Audi", "Германия");
        Brand toyota = brand("Toyota", "Япония");
        Brand kia = brand("Kia", "Южная Корея");
        Brand hyundai = brand("Hyundai", "Южная Корея");
        Brand lada = brand("Lada", "Россия");
        brandRepository.saveAll(java.util.List.of(bmw, mb, audi, toyota, kia, hyundai, lada));

        // -------------------- Автомобили --------------------
        String imgBmwX5 = "file:///android_asset/cars/imgBmwX5.png";
        String imgBmw320 = "file:///android_asset/cars/imgBmw320.png";
        String imgE200   = "file:///android_asset/cars/imgE200.png";
        String imgGlc   = "file:///android_asset/cars/imgGlc.png";
        String imgA4 = "file:///android_asset/cars/imgA4.png";
        String imgQ5 = "file:///android_asset/cars/imgQ5.png";
        String imgCamry = "file:///android_asset/cars/imgCamry.png";
        String imgRav4 = "file:///android_asset/cars/imgRav4.png";
        String imgK5 = "file:///android_asset/cars/imgK5.png";
        String imgSportage = "file:///android_asset/cars/imgSportage.png";
        String imgSonata = "file:///android_asset/cars/imgSonata.png";
        String imgVesta = "file:///android_asset/cars/imgVesta.png";
        String imgM3 = "file:///android_asset/cars/imgM3.png";
        String imgS500 = "file:///android_asset/cars/imgS500.png";

        Vehicle x5 = vehicle(bmw, "X5 xDrive40i", 2024, "WBAJA9105LBJ70001", "8990000",
                BodyType.SUV, EngineType.PETROL, Transmission.AUTOMATIC, DriveType.AWD, "Чёрный", 15, 340,
                "3.0", "9.8", "M Sport", VehicleStatus.IN_STOCK,
                "Премиальный кроссовер с рядным 6-цилиндровым двигателем и полным приводом xDrive.", imgBmwX5);

        Vehicle bmw320 = vehicle(bmw, "320i", 2023, "WBA5R1109MFH10002", "4350000",
                BodyType.SEDAN, EngineType.PETROL, Transmission.AUTOMATIC, DriveType.RWD, "Белый", 120, 184,
                "2.0", "6.6", "Luxury", VehicleStatus.IN_STOCK,
                "Классический бизнес-седан BMW 3 серии с задним приводом.", imgBmw320);

        Vehicle e200 = vehicle(mb, "E 200", 2024, "W1K2130461A100003", "6790000",
                BodyType.SEDAN, EngineType.PETROL, Transmission.AUTOMATIC, DriveType.RWD, "Серебристый", 10, 197,
                "2.0", "7.2", "AMG Line", VehicleStatus.IN_STOCK,
                "Бизнес-седан Mercedes E-Class с системой мягкого гибрида EQ Boost.", imgE200);

        Vehicle glc = vehicle(mb, "GLC 300 4MATIC", 2023, "W1N2539801V100004", "7250000",
                BodyType.CROSSOVER, EngineType.HYBRID, Transmission.AUTOMATIC, DriveType.AWD, "Синий", 45, 258,
                "2.0", "8.1", "Premium", VehicleStatus.IN_STOCK,
                "Гибридный кроссовер премиум-класса с полным приводом 4MATIC.", imgGlc);

        Vehicle a4 = vehicle(audi, "A4 40 TFSI quattro", 2023, "WAUZZZF40PN100005", "4690000",
                BodyType.SEDAN, EngineType.PETROL, Transmission.ROBOT, DriveType.AWD, "Серый", 60, 204,
                "2.0", "6.9", "S line", VehicleStatus.IN_STOCK,
                "Седан Audi A4 с полным приводом quattro и роботизированной КПП S tronic.", imgA4);

        Vehicle q5 = vehicle(audi, "Q5 45 TFSI", 2024, "WA1ZZZFY5P2100006", "6390000",
                BodyType.SUV, EngineType.PETROL, Transmission.ROBOT, DriveType.AWD, "Чёрный", 5, 249,
                "2.0", "8.4", "S line", VehicleStatus.IN_STOCK,
                "Среднеразмерный премиальный кроссовер Audi Q5.", imgQ5);

        Vehicle camry = vehicle(toyota, "Camry 2.5", 2024, "JTNB11HK0P3100007", "3590000",
                BodyType.SEDAN, EngineType.PETROL, Transmission.AUTOMATIC, DriveType.FWD, "Белый", 8, 200,
                "2.5", "7.4", "Prestige", VehicleStatus.IN_STOCK,
                "Самый популярный бизнес-седан в России — Toyota Camry.", imgCamry);

        Vehicle rav4 = vehicle(toyota, "RAV4 2.0", 2023, "JTMB11CA0PD100008", "3290000",
                BodyType.CROSSOVER, EngineType.PETROL, Transmission.CVT, DriveType.AWD, "Серый", 90, 149,
                "2.0", "7.0", "Comfort", VehicleStatus.IN_STOCK,
                "Надёжный и практичный городской кроссовер Toyota RAV4.", imgRav4);

        Vehicle k5 = vehicle(kia, "K5 2.5 GT-Line", 2023, "KNAL341CAP5100009", "3190000",
                BodyType.SEDAN, EngineType.PETROL, Transmission.AUTOMATIC, DriveType.FWD, "Красный", 30, 194,
                "2.5", "7.8", "GT-Line", VehicleStatus.IN_STOCK,
                "Стильный седан Kia K5 в спортивном исполнении GT-Line.", imgK5);

        Vehicle sportage = vehicle(kia, "Sportage 2.0", 2024, "KNAPH81ABP7100010", "3090000",
                BodyType.CROSSOVER, EngineType.PETROL, Transmission.AUTOMATIC, DriveType.AWD, "Зелёный", 12, 150,
                "2.0", "8.0", "Prestige", VehicleStatus.IN_STOCK,
                "Современный кроссовер Kia Sportage нового поколения.", imgSportage);

        Vehicle sonata = vehicle(hyundai, "Sonata 2.5", 2023, "KMHL341CBPA100011", "2990000",
                BodyType.SEDAN, EngineType.PETROL, Transmission.AUTOMATIC, DriveType.FWD, "Чёрный", 25, 180,
                "2.5", "7.6", "Style", VehicleStatus.IN_STOCK,
                "Просторный и технологичный седан Hyundai Sonata.", imgSonata);

        Vehicle vesta = vehicle(lada, "Vesta SW Cross", 2024, "XTAGFL110P1100013", "1890000",
                BodyType.WAGON, EngineType.PETROL, Transmission.MANUAL, DriveType.FWD, "Оранжевый", 5, 106,
                "1.6", "8.2", "Enjoy", VehicleStatus.IN_STOCK,
                "Универсал повышенной проходимости Lada Vesta SW Cross.", imgVesta);

        Vehicle m3 = vehicle(bmw, "M3 Competition", 2023, "WBS43AY0XPFL10015", "11500000",
                BodyType.SEDAN, EngineType.PETROL, Transmission.AUTOMATIC, DriveType.AWD, "Синий", 8000, 510,
                "3.0", "10.1", "Competition", VehicleStatus.RESERVED,
                "Высокопроизводительный спортивный седан BMW M3 Competition xDrive.", imgM3);

        Vehicle s500 = vehicle(mb, "S 500 4MATIC", 2022, "W1K2231761A100016", "13900000",
                BodyType.SEDAN, EngineType.HYBRID, Transmission.AUTOMATIC, DriveType.AWD, "Чёрный", 21000, 435,
                "3.0", "8.9", "First Class", VehicleStatus.SOLD,
                "Флагманский представительский седан Mercedes-Benz S-Class.", imgS500);

        vehicleRepository.saveAll(java.util.List.of(
                x5, bmw320, e200, glc, a4, q5, camry, rav4, k5, sportage, sonata, vesta, m3, s500));

        // -------------------- Избранное --------------------
        favoriteRepository.save(new Favorite(client1, x5));
        favoriteRepository.save(new Favorite(client1, camry));
        favoriteRepository.save(new Favorite(client2, e200));
        favoriteRepository.save(new Favorite(client3, k5));

        // -------------------- Тест-драйвы --------------------
        TestDrive td1 = new TestDrive();
        td1.setUser(client1); td1.setVehicle(x5); td1.setManager(manager1);
        td1.setDealerCenter("Автосалон на Ленина, 100");
        td1.setScheduledAt(LocalDateTime.now().plusDays(2));
        td1.setStatus(TestDriveStatus.CONFIRMED);
        td1.setContactPhone("+79280000004");
        td1.setNotes("Интересует версия M Sport");
        testDriveRepository.save(td1);

        TestDrive td2 = new TestDrive();
        td2.setUser(client2); td2.setVehicle(e200);
        td2.setDealerCenter("Автосалон на Мира, 25");
        td2.setScheduledAt(LocalDateTime.now().plusDays(3));
        td2.setStatus(TestDriveStatus.PENDING);
        td2.setContactPhone("+79280000005");
        testDriveRepository.save(td2);

        // -------------------- Заказы --------------------
        Order order1 = new Order();
        order1.setUser(client3); order1.setVehicle(s500); order1.setManager(manager2);
        order1.setPaymentType(PaymentType.FULL);
        order1.setStatus(OrderStatus.COMPLETED);
        order1.setTotalPrice(s500.getPrice());
        orderRepository.save(order1);

        Order order2 = new Order();
        order2.setUser(client2); order2.setVehicle(m3); order2.setManager(manager1);
        order2.setPaymentType(PaymentType.INSTALLMENT);
        order2.setStatus(OrderStatus.CONFIRMED);
        order2.setTotalPrice(m3.getPrice());
        InstallmentPlan plan = InstallmentPlan.calculate(m3.getPrice(), new BigDecimal("3450000"), 36, new BigDecimal("12.50"));
        order2.attachInstallmentPlan(plan);
        orderRepository.save(order2);

        // -------------------- Отзывы --------------------
        reviewRepository.save(new Review(client3, s500, 5, "Превосходный автомобиль, обслуживание на высоте!"));
        reviewRepository.save(new Review(client1, camry, 4, "Отличная динамика и комплектация за свои деньги."));

        // -------------------- Уведомления --------------------
        notificationRepository.save(new Notification(client1, "Тест-драйв подтверждён",
                "Ваша запись на тест-драйв BMW X5 подтверждена менеджером.", NotificationType.TEST_DRIVE));
        notificationRepository.save(new Notification(client2, "Заявка на рассрочку",
                "Ваша заявка на покупку BMW M3 в рассрочку принята в обработку.", NotificationType.INSTALLMENT));

        log.info("Демонстрационные данные успешно загружены: {} пользователей, {} автомобилей.",
                userRepository.count(), vehicleRepository.count());
    }

    // ----------------------------- helpers -----------------------------

    private User user(String fullName, String email, String rawPassword, String phone, Role role, LoyaltyLevel loyalty) {
        User u = new User();
        u.setFullName(fullName);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(rawPassword));
        u.setPhone(phone);
        u.setRole(role);
        u.setLoyaltyLevel(loyalty);
        u.setPdnConsent(true);
        u.setActive(true);
        return u;
    }

    private Brand brand(String name, String country) {
        return new Brand(name, country);
    }

    private Vehicle vehicle(Brand brand, String model, int year, String vin, String price,
                            BodyType body, EngineType engine, Transmission tr, DriveType drive,
                            String color, int mileage, int power, String volume, String fuel,
                            String equipment, VehicleStatus status, String description, String imageUrl) {
        Vehicle v = new Vehicle();
        v.setBrand(brand);
        v.setModel(model);
        v.setYear(year);
        v.setVin(vin);
        v.setPrice(new BigDecimal(price));
        v.setBodyType(body);
        v.setEngineType(engine);
        v.setTransmission(tr);
        v.setDriveType(drive);
        v.setColor(color);
        v.setMileage(mileage);
        v.setPowerHp(power);
        v.setEngineVolume(new BigDecimal(volume));
        v.setFuelConsumption(new BigDecimal(fuel));
        v.setEquipmentLevel(equipment);
        v.setStatus(status);
        v.setDescription(description);
        v.setImageUrl(imageUrl);
        return v;
    }
}
