package com.rapido.booking.service;

import com.rapido.booking.dto.RideRequestDto;
import com.rapido.booking.dto.RideResponseDto;

public interface RideService {

    RideResponseDto requestRide(RideRequestDto requestDto);

    RideResponseDto acceptRide(Long tripId);

    RideResponseDto startRide(Long tripId);

    RideResponseDto completeRide(Long tripId);
}
