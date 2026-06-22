package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.dashboard.DashboardResponse;

/** Mediator: сводная аналитика для администратора/менеджера. */
public interface DashboardService {

    DashboardResponse getStats();
}
