package com.rapido.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RiderRequestDto {

    @NotBlank(message = "name is required")
    private String name;
}
