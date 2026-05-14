package com.rapido.booking.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RiderResponseDto {
    private Long id;
    private String name;
}
