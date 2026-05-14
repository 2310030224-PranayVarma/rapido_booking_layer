package com.rapido.booking.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DriverResponseDto {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Boolean availability;
    private Double rating;
}
