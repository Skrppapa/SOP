package edu.rutmiit.demo.controllers;

import edu.rutmiit.demo.fit_contract.dto.MembershipRequest;
import edu.rutmiit.demo.fit_contract.dto.MembershipResponse;
import edu.rutmiit.demo.fit_contract.dto.PagedResponse;
import edu.rutmiit.demo.fit_contract.endpoints.MembershipApi;
import edu.rutmiit.demo.services.MembershipService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController implements MembershipApi {
    private final MembershipService service;

    public MembershipController(MembershipService service) {
        this.service = service;
    }


    @Override
    @GetMapping
    public PagedResponse<MembershipResponse> getAllMemberships(
            @RequestParam(required = false) String membershipNumber,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) Long clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.findAll(membershipNumber, level, clientId, page, size);
    }

    @Override
    @GetMapping("/{id}")
    public MembershipResponse getMembershipById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Override
    @PostMapping
    public MembershipResponse createMembership(@RequestBody MembershipRequest request) {
        return service.create(request);
    }

    @Override
    @PutMapping("/{id}")
    public MembershipResponse updateMembership(@PathVariable Long id, @RequestBody MembershipRequest request) {
        return service.update(id, request);
    }

    @Override
    @DeleteMapping("/{id}")
    public void deleteMembership(@PathVariable Long id) {
        service.delete(id);
    }
}