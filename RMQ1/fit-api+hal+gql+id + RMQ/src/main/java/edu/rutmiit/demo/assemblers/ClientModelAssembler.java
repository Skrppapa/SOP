package edu.rutmiit.demo.assemblers;

import edu.rutmiit.demo.controllers.ClientController;
import edu.rutmiit.demo.controllers.MembershipController;
import edu.rutmiit.demo.fit_contract.dto.ClientResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClientModelAssembler implements RepresentationModelAssembler<ClientResponse, EntityModel<ClientResponse>> {

    @Override
    public EntityModel<ClientResponse> toModel(ClientResponse client) {
        return EntityModel.of(client,
                linkTo(methodOn(ClientController.class).getClient(client.getId())).withSelfRel(),
                linkTo(methodOn(MembershipController.class).getAllMemberships(null, null, client.getId(), 0, 10)).withRel("memberships"),
                linkTo(methodOn(ClientController.class).getAllClients(null, null, null, 0, 10)).withRel("collection")
        );
    }
}