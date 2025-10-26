package edu.rutmiit.demo.fit_contract.dto;

import java.time.LocalDate;

public record ClientResponse(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String city
) {}