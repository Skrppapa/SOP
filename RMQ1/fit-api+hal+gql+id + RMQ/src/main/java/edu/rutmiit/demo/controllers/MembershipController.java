package edu.rutmiit.demo.controllers;

import edu.rutmiit.demo.assemblers.MembershipModelAssembler;
import edu.rutmiit.demo.fit_contract.dto.MembershipRequest;
import edu.rutmiit.demo.fit_contract.dto.MembershipResponse;
import edu.rutmiit.demo.fit_contract.dto.PagedResponse;
import edu.rutmiit.demo.fit_contract.endpoints.MembershipApi;
import edu.rutmiit.demo.services.MembershipService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController implements MembershipApi {
    private final MembershipService service;
    private final MembershipModelAssembler membershipModelAssembler;
    private final PagedResourcesAssembler<MembershipResponse> pagedResourcesAssembler;

    public MembershipController(MembershipService service,
                                MembershipModelAssembler membershipModelAssembler,
                                PagedResourcesAssembler<MembershipResponse> pagedResourcesAssembler) {
        this.service = service;
        this.membershipModelAssembler = membershipModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @Override
    @GetMapping
    public PagedModel<EntityModel<MembershipResponse>> getAllMemberships(
            @RequestParam(required = false) String membershipNumber,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponse<MembershipResponse> pagedResponse = service.findAll(membershipNumber, level, clientId, page, size);
        Page<MembershipResponse> membershipPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return pagedResourcesAssembler.toModel(membershipPage, membershipModelAssembler);
    }

    @Override
    @GetMapping("/{id}")
    public EntityModel<MembershipResponse> getMembershipById(@PathVariable Long id) {
        MembershipResponse membership = service.findById(id);
        return membershipModelAssembler.toModel(membership);
    }

    @Override
    @PostMapping
    public ResponseEntity<EntityModel<MembershipResponse>> createMembership(@RequestBody MembershipRequest request) {
        MembershipResponse createdMembership = service.create(request);
        EntityModel<MembershipResponse> entityModel = membershipModelAssembler.toModel(createdMembership);

        return ResponseEntity
                .created(entityModel.getRequiredLink("self").toUri())
                .body(entityModel);
    }

    @Override
    @PutMapping("/{id}")
    public EntityModel<MembershipResponse> updateMembership(@PathVariable Long id, @RequestBody MembershipRequest request) {
        MembershipResponse updatedMembership = service.update(id, request);
        return membershipModelAssembler.toModel(updatedMembership);
    }

    @Override
    @DeleteMapping("/{id}")
    public void deleteMembership(@PathVariable Long id) {
        service.delete(id);
    }
}