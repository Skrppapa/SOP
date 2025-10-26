package edu.rutmiit.demo.graphql;

import com.netflix.graphql.dgs.*;
import edu.rutmiit.demo.fit_contract.dto.MembershipRequest;
import edu.rutmiit.demo.fit_contract.dto.MembershipResponse;
import edu.rutmiit.demo.fit_contract.dto.ClientResponse;
import edu.rutmiit.demo.fit_contract.dto.PagedResponse;
import edu.rutmiit.demo.services.MembershipService;
import edu.rutmiit.demo.services.ClientService;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@DgsComponent
public class MembershipDataFetcher {

    private final MembershipService membershipService;
    private final ClientService clientService;

    @Autowired
    public MembershipDataFetcher(MembershipService membershipService, ClientService clientService) {
        this.membershipService = membershipService;
        this.clientService = clientService;
    }

    @DgsQuery
    public PagedResponse<MembershipResponse> memberships(
            @InputArgument String membershipNumber,
            @InputArgument String level,
            @InputArgument Long clientId,
            @InputArgument int page,
            @InputArgument int size) {
        return membershipService.findAll(membershipNumber, level, clientId, page, size);
    }

    @DgsQuery
    public MembershipResponse membershipById(@InputArgument Long id) {
        return membershipService.findById(id);
    }

    @DgsQuery
    public MembershipResponse membershipByNumber(@InputArgument String membershipNumber) {
        return membershipService.findByMembershipNumber(membershipNumber);
    }

    @DgsQuery
    public java.util.List<MembershipResponse> clientMemberships(@InputArgument Long clientId) {
        return membershipService.findByClientId(clientId);
    }

    @DgsMutation
    public MembershipResponse createMembership(@InputArgument("input") Map<String, Object> input) {
        MembershipRequest request = new MembershipRequest(
                (String) input.get("membershipNumber"),
                (String) input.get("duration"),
                (String) input.get("level"),
                Long.parseLong(input.get("clientId").toString())
        );
        return membershipService.create(request);
    }

    @DgsMutation
    public MembershipResponse updateMembership(@InputArgument Long id, @InputArgument("input") Map<String, Object> input) {
        MembershipRequest request = new MembershipRequest(
                (String) input.get("membershipNumber"),
                (String) input.get("duration"),
                (String) input.get("level"),
                Long.parseLong(input.get("clientId").toString())
        );
        return membershipService.update(id, request);
    }

    @DgsMutation
    public Long deleteMembership(@InputArgument Long id) {
        membershipService.delete(id);
        return id;
    }

    @DgsMutation
    public MembershipResponse renewMembership(@InputArgument Long id, @InputArgument String newDuration) {
        // Реализуйте метод в сервисе для продления абонемента
        return membershipService.renewMembership(id, newDuration);
    }

    @DgsMutation
    public MembershipResponse changeMembershipLevel(@InputArgument Long id, @InputArgument String newLevel) {
        // Реализуйте метод в сервисе для изменения уровня
        return membershipService.changeMembershipLevel(id, newLevel);
    }

    // Этот метод разрешает вложенное поле 'client' внутри типа 'Membership'
    @DgsData(parentType = "Membership", field = "client")
    public ClientResponse client(DataFetchingEnvironment dfe) {
        MembershipResponse membership = dfe.getSource();
        return clientService.findById(membership.getClientID());
    }
}