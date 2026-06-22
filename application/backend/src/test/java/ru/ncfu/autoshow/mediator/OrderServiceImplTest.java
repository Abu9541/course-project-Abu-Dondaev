package ru.ncfu.autoshow.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.autoshow.dto.order.InstallmentCalcRequest;
import ru.ncfu.autoshow.dto.order.InstallmentPlanResponse;
import ru.ncfu.autoshow.dto.order.OrderRequest;
import ru.ncfu.autoshow.dto.order.OrderResponse;
import ru.ncfu.autoshow.entity.Brand;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.entity.enums.*;
import ru.ncfu.autoshow.exception.BusinessRuleException;
import ru.ncfu.autoshow.foundation.FavoriteRepository;
import ru.ncfu.autoshow.foundation.OrderRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.OrderMapper;
import ru.ncfu.autoshow.mediator.impl.OrderServiceImpl;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис заказов (бизнес-логика покупки)")
class OrderServiceImplTest {

    @Mock OrderRepository orderRepository;
    @Mock VehicleRepository vehicleRepository;
    @Mock UserRepository userRepository;
    @Mock FavoriteRepository favoriteRepository;
    @Mock NotificationService notificationService;

    OrderServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrderServiceImpl(orderRepository, vehicleRepository, userRepository,
                favoriteRepository, notificationService, new OrderMapper());
    }

    private User client() {
        User u = new User();
        u.setId(4L);
        u.setFullName("Магомед Дудаев");
        u.setRole(Role.CLIENT);
        return u;
    }

    private Vehicle vehicle(VehicleStatus status, String price) {
        Vehicle v = new Vehicle();
        v.setId(1L);
        v.setBrand(new Brand("Toyota", "Япония"));
        v.setModel("Camry");
        v.setYear(2024);
        v.setPrice(new BigDecimal(price));
        v.setStatus(status);
        return v;
    }

    @Test
    @DisplayName("Покупка с полной оплатой переводит автомобиль в RESERVED")
    void buyFullReservesVehicle() {
        User client = client();
        Vehicle v = vehicle(VehicleStatus.IN_STOCK, "3590000");
        when(userRepository.findById(4L)).thenReturn(Optional.of(client));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(v));
        when(orderRepository.existsByVehicleIdAndStatusIn(eq(1L), anyList())).thenReturn(false);
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse resp = service.buy(4L, new OrderRequest(1L, PaymentType.FULL, null));

        assertEquals(VehicleStatus.RESERVED, v.getStatus());
        assertEquals(PaymentType.FULL, resp.paymentType());
        assertEquals(0, resp.totalPrice().compareTo(new BigDecimal("3590000")));
        verify(notificationService).notify(eq(client), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Рассрочка без параметров приводит к нарушению бизнес-правила")
    void buyInstallmentWithoutParamsThrows() {
        when(userRepository.findById(4L)).thenReturn(Optional.of(client()));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(VehicleStatus.IN_STOCK, "3590000")));
        when(orderRepository.existsByVehicleIdAndStatusIn(anyLong(), anyList())).thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> service.buy(4L, new OrderRequest(1L, PaymentType.INSTALLMENT, null)));
    }

    @Test
    @DisplayName("Нельзя купить недоступный (проданный) автомобиль")
    void buyUnavailableThrows() {
        when(userRepository.findById(4L)).thenReturn(Optional.of(client()));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(VehicleStatus.SOLD, "3590000")));

        assertThrows(BusinessRuleException.class,
                () -> service.buy(4L, new OrderRequest(1L, PaymentType.FULL, null)));
    }

    @Test
    @DisplayName("Калькулятор рассрочки возвращает положительный платёж")
    void calculateReturnsPositivePayment() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle(VehicleStatus.IN_STOCK, "3000000")));

        InstallmentPlanResponse r = service.calculate(new InstallmentCalcRequest(1L, new BigDecimal("600000"), 24));

        assertEquals(24, r.termMonths());
        assertTrue(r.monthlyPayment().signum() > 0);
        assertTrue(r.overpayment().signum() >= 0);
    }
}
