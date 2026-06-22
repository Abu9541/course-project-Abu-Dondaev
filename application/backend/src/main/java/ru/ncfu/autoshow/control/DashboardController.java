package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ncfu.autoshow.dto.dashboard.DashboardResponse;
import ru.ncfu.autoshow.mediator.DashboardService;

/** Control: сводная аналитика для персонала. */
@Tag(name = "Аналитика", description = "Сводная статистика автосалона")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Сводная статистика (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping
    public DashboardResponse stats() {
        return dashboardService.getStats();
    }
}
