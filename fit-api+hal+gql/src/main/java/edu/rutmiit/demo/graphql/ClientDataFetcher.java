package edu.rutmiit.demo.graphql;

import com.netflix.graphql.dgs.*;
import edu.rutmiit.demo.fit_contract.dto.ClientRequest;
import edu.rutmiit.demo.fit_contract.dto.ClientResponse;
import edu.rutmiit.demo.fit_contract.dto.MembershipResponse;
import edu.rutmiit.demo.fit_contract.dto.PagedResponse;
import edu.rutmiit.demo.services.ClientService;
import edu.rutmiit.demo.services.MembershipService;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import com.netflix.graphql.dgs.*;


import java.util.List;
import java.util.Map;

@DgsComponent
public class ClientDataFetcher {

    private final ClientService clientService;
    private final MembershipService membershipService;

    @Autowired
    public ClientDataFetcher(ClientService clientService, MembershipService membershipService) {
        this.clientService = clientService;
        this.membershipService = membershipService;
    }

    @DgsQuery
    public PagedResponse<ClientResponse> clients(
            @InputArgument String city,
            @InputArgument String firstName,
            @InputArgument String lastName,
            @InputArgument int page,
            @InputArgument int size) {
        return clientService.findAll(city, firstName, lastName, page, size);
    }

    @DgsQuery
    public ClientResponse clientById(@InputArgument Long id) {
        return clientService.findById(id);
    }

    @DgsMutation
    public ClientResponse createClient(@InputArgument("input") Map<String, Object> input) {
        ClientRequest request = new ClientRequest(
                (String) input.get("firstName"),
                (String) input.get("lastName"),
                java.time.LocalDate.parse((String) input.get("birthDate")),
                (String) input.get("city")
        );
        return clientService.create(request);
    }

    @DgsMutation
    public ClientResponse updateClient(@InputArgument Long id, @InputArgument("input") Map<String, Object> input) {
        ClientRequest request = new ClientRequest(
                (String) input.get("firstName"),
                (String) input.get("lastName"),
                java.time.LocalDate.parse((String) input.get("birthDate")),
                (String) input.get("city")
        );
        return clientService.update(id, request);
    }

    @DgsMutation
    public Long deleteClient(@InputArgument Long id) {
        clientService.delete(id);
        return id;
    }

    // Этот метод разрешает вложенное поле 'memberships' внутри типа 'Client'
    @DgsData(parentType = "Client", field = "memberships")
    public List<MembershipResponse> memberships(DataFetchingEnvironment dfe) {
        ClientResponse client = dfe.getSource();
        return membershipService.findByClientId(client.getId());
    }
}