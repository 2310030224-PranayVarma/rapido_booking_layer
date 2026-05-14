package com.rapido.booking.dto;

import java.time.LocalDateTime;

import com.rapido.booking.entity.TripStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RideResponseDto {
    private Long tripId;
    private Long riderId;
    private Long driverId;
    private String driverName;
    private TripStatus status;
    private String pickupLocation;
    private LocalDateTime createdAt;
}
