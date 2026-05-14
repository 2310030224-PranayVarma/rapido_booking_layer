package com.rapido.booking.service;

import java.util.List;

import com.rapido.booking.dto.DriverRequestDto;
import com.rapido.booking.dto.DriverResponseDto;

public interface DriverService {

    DriverResponseDto createDriver(DriverRequestDto requestDto);

    List<DriverResponseDto> getAvailableDrivers();
}
