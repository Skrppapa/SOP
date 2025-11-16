package edu.rutmiit.demo.fit_contract.endpoints;

import edu.rutmiit.demo.fit_contract.dto.MembershipRequest;
import edu.rutmiit.demo.fit_contract.dto.MembershipResponse;
import edu.rutmiit.demo.fit_contract.dto.StatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Абонементы", description = "API для работы с абонементами")
@RequestMapping("/api/memberships")
public interface MembershipApi {

    @Operation(summary = "Получить абонемент по ID")
    @ApiResponse(responseCode = "200", description = "Абонемент найден")
    @ApiResponse(responseCode = "404", description = "Абонемент не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<MembershipResponse> getMembershipById(@PathVariable("id") Long id);

    @Operation(summary = "Получить список всех абонементов с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Список абонементов")
    @GetMapping
    PagedModel<EntityModel<MembershipResponse>> getAllMemberships(
            @Parameter(description = "Фильтр по номеру абонемента") @RequestParam(required = false) String membershipNumber,
            @Parameter(description = "Фильтр по уровню абонемента") @RequestParam(required = false) String level,
            @Parameter(description = "Фильтр по ID клиента") @RequestParam(required = false) Long clientId,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    );

    @Operation(summary = "Создать новый абонемент")
    @ApiResponse(responseCode = "201", description = "Абонемент успешно создан")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Абонемент с таким номером уже существует", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    ResponseEntity<EntityModel<MembershipResponse>> createMembership(@Valid @RequestBody MembershipRequest request);

    @Operation(summary = "Обновить абонемент по ID")
    @ApiResponse(responseCode = "200", description = "Абонемент успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Абонемент не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Абонемент с таким номером уже существует", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<MembershipResponse> updateMembership(@PathVariable Long id, @Valid @RequestBody MembershipRequest request);

    @Operation(summary = "Удалить абонемент по ID")
    @ApiResponse(responseCode = "204", description = "Абонемент успешно удален")
    @ApiResponse(responseCode = "404", description = "Абонемент не найден")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteMembership(@PathVariable Long id);
}