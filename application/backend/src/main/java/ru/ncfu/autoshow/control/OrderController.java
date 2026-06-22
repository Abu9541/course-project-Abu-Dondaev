package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.order.InstallmentCalcRequest;
import ru.ncfu.autoshow.dto.order.InstallmentPlanResponse;
import ru.ncfu.autoshow.dto.order.OrderRequest;
import ru.ncfu.autoshow.dto.order.OrderResponse;
import ru.ncfu.autoshow.mediator.OrderService;
import ru.ncfu.autoshow.security.CustomUserDetails;

import java.util.List;

/** Control: покупка автомобилей (полная оплата и рассрочка). */
@Tag(name = "Заказы", description = "Покупка автомобилей, рассрочка и обработка заказов")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Калькулятор рассрочки (предварительный расчёт)")
    @PostMapping("/calculate")
    public InstallmentPlanResponse calculate(@Valid @RequestBody InstallmentCalcRequest request) {
        return orderService.calculate(request);
    }

    @Operation(summary = "Оформить покупку (полная оплата или рассрочка)")
    @PostMapping
    public ResponseEntity<OrderResponse> buy(@AuthenticationPrincipal CustomUserDetails principal,
                                             @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.buy(principal.getId(), request));
    }

    @Operation(summary = "Мои заказы")
    @GetMapping("/my")
    public List<OrderResponse> my(@AuthenticationPrincipal CustomUserDetails principal) {
        return orderService.getMine(principal.getId());
    }

    @Operation(summary = "Все заказы (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping
    public List<OrderResponse> all() {
        return orderService.getAll();
    }

    @Operation(summary = "Подтвердить заказ (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/{id}/confirm")
    public OrderResponse confirm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        return orderService.confirm(id, principal.getId());
    }

    @Operation(summary = "Отметить оплату (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/{id}/pay")
    public OrderResponse pay(@PathVariable Long id) {
        return orderService.markPaid(id);
    }

    @Operation(summary = "Завершить сделку (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/{id}/complete")
    public OrderResponse complete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        return orderService.complete(id, principal.getId());
    }

    @Operation(summary = "Отменить заказ (владелец или персонал)")
    @PostMapping("/{id}/cancel")
    public OrderResponse cancel(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        return orderService.cancel(id, principal.getId(), principal.getUser().isStaff());
    }
}
