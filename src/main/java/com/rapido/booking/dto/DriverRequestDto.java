package com.rapido.booking.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverRequestDto {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "latitude is required")
    private Double latitude;

    @NotNull(message = "longitude is required")
    private Double longitude;

    @NotNull(message = "availability is required")
    private Boolean availability;

    @NotNull(message = "rating is required")
    @DecimalMin(value = "1.0", message = "rating must be at least 1")
    @DecimalMax(value = "5.0", message = "rating must be at most 5")
    private Double rating;
}
