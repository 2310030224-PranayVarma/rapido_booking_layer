package com.rapido.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RideRequestDto {

    @NotNull(message = "riderId is required")
    private Long riderId;

    @NotBlank(message = "pickupLocation is required")
    private String pickupLocation;

    @NotNull(message = "pickupLatitude is required")
    private Double pickupLatitude;

    @NotNull(message = "pickupLongitude is required")
    private Double pickupLongitude;
}
