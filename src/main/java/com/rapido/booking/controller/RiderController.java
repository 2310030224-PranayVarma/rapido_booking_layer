package com.rapido.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rapido.booking.dto.RiderRequestDto;
import com.rapido.booking.dto.RiderResponseDto;
import com.rapido.booking.service.RiderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/riders")
@RequiredArgsConstructor
public class RiderController {

    private final RiderService riderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RiderResponseDto createRider(@Valid @RequestBody RiderRequestDto requestDto) {
        return riderService.createRider(requestDto);
    }
}
