package edu.rutmiit.demo.controllers;

import edu.rutmiit.demo.fit_contract.dto.ClientRequest;
import edu.rutmiit.demo.fit_contract.dto.ClientResponse;
import edu.rutmiit.demo.fit_contract.dto.PagedResponse;
import edu.rutmiit.demo.fit_contract.endpoints.ClientApi;
import edu.rutmiit.demo.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController implements ClientApi {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @Override
    public PagedResponse<ClientResponse> getAllClients(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Обратите внимание на порядок параметров - он должен совпадать с контрактом
        return service.findAll(city, firstName, lastName, page, size);
    }

    @Override
    public ClientResponse getClient(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @Override
    public ClientResponse createClient(@Valid @RequestBody ClientRequest clientRequest) {
        return service.create(clientRequest);
    }

    @Override
    public ClientResponse updateClient(@PathVariable Long id, @Valid @RequestBody ClientRequest clientRequest) {
        return service.update(id, clientRequest);
    }

    @Override
    public void deleteClient(@PathVariable Long id) {
        service.delete(id);
        // Метод void, статус 204 устанавливается через @ResponseStatus в контракте
    }
}