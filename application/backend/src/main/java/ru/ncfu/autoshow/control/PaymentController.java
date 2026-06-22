package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.payment.ConfirmPaymentRequest;
import ru.ncfu.autoshow.dto.payment.PaymentResponse;
import ru.ncfu.autoshow.mediator.PaymentService;
import ru.ncfu.autoshow.security.CustomUserDetails;

/** Control: оплата заказов банковской картой (имитация платёжного шлюза). */
@Tag(name = "Оплата", description = "Оплата заказов картой (имитация платёжного шлюза)")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Создать платёж для своего заказа")
    @PostMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> create(@PathVariable Long orderId,
                                                  @AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createForOrder(orderId, principal.getId()));
    }

    @Operation(summary = "Подтвердить платёж картой")
    @PostMapping("/{providerPaymentId}/confirm")
    public PaymentResponse confirm(@PathVariable String providerPaymentId,
                                   @AuthenticationPrincipal CustomUserDetails principal,
                                   @Valid @RequestBody ConfirmPaymentRequest request) {
        return paymentService.confirm(providerPaymentId, principal.getId(), request);
    }
}
