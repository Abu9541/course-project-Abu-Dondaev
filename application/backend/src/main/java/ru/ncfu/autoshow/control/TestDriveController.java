package ru.ncfu.autoshow.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.ncfu.autoshow.dto.testdrive.RescheduleRequest;
import ru.ncfu.autoshow.dto.testdrive.TestDriveRequest;
import ru.ncfu.autoshow.dto.testdrive.TestDriveResponse;
import ru.ncfu.autoshow.mediator.TestDriveService;
import ru.ncfu.autoshow.security.CustomUserDetails;

import java.util.List;

/** Control: записи на тест-драйв. */
@Tag(name = "Тест-драйвы", description = "Запись на тест-драйв и обработка заявок")
@RestController
@RequestMapping("/api/test-drives")
public class TestDriveController {

    private final TestDriveService testDriveService;

    public TestDriveController(TestDriveService testDriveService) {
        this.testDriveService = testDriveService;
    }

    @Operation(summary = "Записаться на тест-драйв")
    @PostMapping
    public ResponseEntity<TestDriveResponse> book(@AuthenticationPrincipal CustomUserDetails principal,
                                                  @Valid @RequestBody TestDriveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testDriveService.book(principal.getId(), request));
    }

    @Operation(summary = "Мои записи на тест-драйв")
    @GetMapping("/my")
    public List<TestDriveResponse> my(@AuthenticationPrincipal CustomUserDetails principal) {
        return testDriveService.getMine(principal.getId());
    }

    @Operation(summary = "Все записи (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @GetMapping
    public List<TestDriveResponse> all() {
        return testDriveService.getAll();
    }

    @Operation(summary = "Подтвердить запись (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/{id}/confirm")
    public TestDriveResponse confirm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        return testDriveService.confirm(id, principal.getId());
    }

    @Operation(summary = "Отклонить запись (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/{id}/reject")
    public TestDriveResponse reject(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        return testDriveService.reject(id, principal.getId());
    }

    @Operation(summary = "Завершить запись (менеджер/админ)")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    @PostMapping("/{id}/complete")
    public TestDriveResponse complete(@PathVariable Long id) {
        return testDriveService.complete(id);
    }

    @Operation(summary = "Отменить запись (владелец или персонал)")
    @PostMapping("/{id}/cancel")
    public TestDriveResponse cancel(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        return testDriveService.cancel(id, principal.getId(), principal.getUser().isStaff());
    }

    @Operation(summary = "Перенести запись (владелец или персонал)")
    @PutMapping("/{id}/reschedule")
    public TestDriveResponse reschedule(@PathVariable Long id,
                                        @AuthenticationPrincipal CustomUserDetails principal,
                                        @Valid @RequestBody RescheduleRequest request) {
        return testDriveService.reschedule(id, principal.getId(), principal.getUser().isStaff(), request);
    }
}
