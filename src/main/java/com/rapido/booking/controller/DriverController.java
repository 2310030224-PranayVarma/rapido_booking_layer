package com.rapido.booking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rapido.booking.dto.DriverRequestDto;
import com.rapido.booking.dto.DriverResponseDto;
import com.rapido.booking.service.DriverService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponseDto createDriver(@Valid @RequestBody DriverRequestDto requestDto) {
        return driverService.createDriver(requestDto);
    }

    @GetMapping("/available")
    public List<DriverResponseDto> getAvailableDrivers() {
        return driverService.getAvailableDrivers();
    }
}
