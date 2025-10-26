package edu.rutmiit.demo.controllers;

import edu.rutmiit.demo.assemblers.ClientModelAssembler;
import edu.rutmiit.demo.fit_contract.dto.ClientRequest;
import edu.rutmiit.demo.fit_contract.dto.ClientResponse;
import edu.rutmiit.demo.fit_contract.dto.PagedResponse;
import edu.rutmiit.demo.fit_contract.endpoints.ClientApi;
import edu.rutmiit.demo.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController implements ClientApi {

    private final ClientService service;
    private final ClientModelAssembler clientModelAssembler;
    private final PagedResourcesAssembler<ClientResponse> pagedResourcesAssembler;

    public ClientController(ClientService service,
                            ClientModelAssembler clientModelAssembler,
                            PagedResourcesAssembler<ClientResponse> pagedResourcesAssembler) {
        this.service = service;
        this.clientModelAssembler = clientModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    public PagedModel<EntityModel<ClientResponse>> getAllClients(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<ClientResponse> pagedResponse = service.findAll(city, firstName, lastName, page, size);
        Page<ClientResponse> clientPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return pagedResourcesAssembler.toModel(clientPage, clientModelAssembler);
    }

    @Override
    public EntityModel<ClientResponse> getClient(@PathVariable("id") Long id) {
        ClientResponse client = service.findById(id);
        return clientModelAssembler.toModel(client);
    }

    @Override
    public ResponseEntity<EntityModel<ClientResponse>> createClient(@Valid @RequestBody ClientRequest clientRequest) {
        ClientResponse createdClient = service.create(clientRequest);
        EntityModel<ClientResponse> entityModel = clientModelAssembler.toModel(createdClient);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    public EntityModel<ClientResponse> updateClient(@PathVariable Long id, @Valid @RequestBody ClientRequest clientRequest) {
        ClientResponse updatedClient = service.update(id, clientRequest);
        return clientModelAssembler.toModel(updatedClient);
    }

    @Override
    public void deleteClient(@PathVariable Long id) {
        service.delete(id);
    }
}