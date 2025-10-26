package edu.rutmiit.demo.fit_contract.dto;

public record MembershipResponse(
        Long id,
        String membershipNumber,
        String duration,
        String level,
        Long clientID
) {}