package ru.ncfu.autoshow.mediator.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.dashboard.DashboardResponse;
import ru.ncfu.autoshow.entity.enums.OrderStatus;
import ru.ncfu.autoshow.entity.enums.TestDriveStatus;
import ru.ncfu.autoshow.entity.enums.VehicleStatus;
import ru.ncfu.autoshow.foundation.*;
import ru.ncfu.autoshow.mediator.DashboardService;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final TestDriveRepository testDriveRepository;

    public DashboardServiceImpl(VehicleRepository vehicleRepository, UserRepository userRepository,
                                OrderRepository orderRepository, TestDriveRepository testDriveRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.testDriveRepository = testDriveRepository;
    }

    @Override
    public DashboardResponse getStats() {
        return new DashboardResponse(
                vehicleRepository.count(),
                vehicleRepository.countByStatus(VehicleStatus.IN_STOCK),
                vehicleRepository.countByStatus(VehicleStatus.RESERVED),
                vehicleRepository.countByStatus(VehicleStatus.SOLD),
                userRepository.count(),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.PENDING),
                orderRepository.countByStatus(OrderStatus.COMPLETED),
                testDriveRepository.countByStatus(TestDriveStatus.PENDING),
                orderRepository.totalRevenue()
        );
    }
}
