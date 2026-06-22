package ru.ncfu.autoshow.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ncfu.autoshow.entity.enums.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Жизненный цикл наличия автомобиля")
class VehicleTest {

    private Vehicle vehicleInStock() {
        Vehicle v = new Vehicle();
        v.setBrand(new Brand("BMW", "Германия"));
        v.setModel("X5");
        v.setYear(2024);
        v.setVin("WBAJA9105LBJ70001");
        v.setPrice(new BigDecimal("8990000"));
        v.setBodyType(BodyType.SUV);
        v.setEngineType(EngineType.PETROL);
        v.setTransmission(Transmission.AUTOMATIC);
        v.setDriveType(DriveType.AWD);
        v.setColor("Чёрный");
        v.setPowerHp(340);
        v.setStatus(VehicleStatus.IN_STOCK);
        return v;
    }

    @Test
    @DisplayName("Доступный автомобиль можно зарезервировать")
    void reserveAvailable() {
        Vehicle v = vehicleInStock();
        assertTrue(v.isAvailable());
        v.reserve();
        assertEquals(VehicleStatus.RESERVED, v.getStatus());
    }

    @Test
    @DisplayName("Нельзя зарезервировать проданный автомобиль")
    void cannotReserveSold() {
        Vehicle v = vehicleInStock();
        v.markSold();
        assertThrows(IllegalStateException.class, v::reserve);
    }

    @Test
    @DisplayName("Возврат в наличие не затрагивает проданный автомобиль")
    void returnToStockKeepsSold() {
        Vehicle v = vehicleInStock();
        v.markSold();
        v.returnToStock();
        assertEquals(VehicleStatus.SOLD, v.getStatus());
    }

    @Test
    @DisplayName("Полное наименование формируется из марки, модели и года")
    void fullName() {
        Vehicle v = vehicleInStock();
        assertEquals("BMW X5, 2024", v.fullName());
    }
}
