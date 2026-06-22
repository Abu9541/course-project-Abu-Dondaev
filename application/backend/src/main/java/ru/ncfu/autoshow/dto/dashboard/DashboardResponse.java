package ru.ncfu.autoshow.dto.dashboard;

import java.math.BigDecimal;

/** Сводная статистика для панели администратора/менеджера. */
public record DashboardResponse(
        long totalVehicles,
        long inStock,
        long reserved,
        long sold,
        long totalUsers,
        long totalOrders,
        long pendingOrders,
        long completedOrders,
        long pendingTestDrives,
        BigDecimal totalRevenue
) {
}
