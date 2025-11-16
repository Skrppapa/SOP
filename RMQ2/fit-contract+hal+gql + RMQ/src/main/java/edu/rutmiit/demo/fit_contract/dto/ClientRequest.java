package edu.rutmiit.demo.fit_contract.dto;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;


public record ClientRequest(

        @NotBlank(message = "Имя клиента обязательно")
        @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов")
        String firstName,

        @NotBlank(message = "Фамилия клиента обязательна")
        @Size(min = 2, max = 50, message = "Фамилия должна быть от 2 до 50 символов")
        String lastName,

        @NotNull(message = "Дата рождения клиента обязательна")
        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate,

        @NotBlank(message = "Город обязателен")
        @Size(min = 2, max = 50, message = "Город должен быть от 2 до 50 символов")
        String city
) {}