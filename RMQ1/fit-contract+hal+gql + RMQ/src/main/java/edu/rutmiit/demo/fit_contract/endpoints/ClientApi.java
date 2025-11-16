package edu.rutmiit.demo.fit_contract.endpoints;

import edu.rutmiit.demo.fit_contract.dto.ClientRequest;
import edu.rutmiit.demo.fit_contract.dto.ClientResponse;
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

@Tag(name = "Клиенты", description = "API для управления клиентами")
@RequestMapping("/api/v1/clients")
public interface ClientApi {

    @Operation(summary = "Создать нового клиента")
    @ApiResponse(responseCode = "201", description = "Клиент успешно создан")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Клиент с такими данными уже существует", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    ResponseEntity<EntityModel<ClientResponse>> createClient(@Valid @RequestBody ClientRequest clientRequest);

    @Operation(summary = "Получить клиента по ID")
    @ApiResponse(responseCode = "200", description = "Клиент найден")
    @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<ClientResponse> getClient(@PathVariable("id") Long id);

    @Operation(summary = "Обновить данные клиента")
    @ApiResponse(responseCode = "200", description = "Данные клиента обновлены")
    @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Конфликт данных", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<ClientResponse> updateClient(@PathVariable Long id, @Valid @RequestBody ClientRequest clientRequest);

    @Operation(summary = "Удалить клиента")
    @ApiResponse(responseCode = "204", description = "Клиент удален")
    @ApiResponse(responseCode = "404", description = "Клиент не найден", content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteClient(@PathVariable Long id);

    @Operation(summary = "Получить всех клиентов с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Список клиентов получен")
    @GetMapping
    PagedModel<EntityModel<ClientResponse>> getAllClients(
            @Parameter(description = "Фильтр по городу") @RequestParam(required = false) String city,
            @Parameter(description = "Фильтр по имени") @RequestParam(required = false) String firstName,
            @Parameter(description = "Фильтр по фамилии") @RequestParam(required = false) String lastName,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    );
}