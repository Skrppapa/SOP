package edu.rutmiit.demo.fit_contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record MembershipRequest(

        @NotBlank(message = "Номер абонемента обязателен")
        @Pattern(regexp = "\\d{10}", message = "Номер абонемента должен содержать ровно 10 цифр")
        String membershipNumber,

        @NotBlank(message = "Продолжительность обязательна")
        String duration,

        @NotBlank(message = "Уровень абонемента обязателен")
        String level,

        @NotNull(message = "ID клиента не может быть пустым")
        Long clientID
) {}