package com.rapido.booking.service;

import com.rapido.booking.dto.RiderRequestDto;
import com.rapido.booking.dto.RiderResponseDto;
import com.rapido.booking.entity.Rider;

public interface RiderService {

    RiderResponseDto createRider(RiderRequestDto requestDto);

    Rider getExistingRider(Long riderId);
}
