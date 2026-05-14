package com.rapido.booking.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rapido.booking.dto.RideRequestDto;
import com.rapido.booking.dto.RideResponseDto;
import com.rapido.booking.service.RideService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public RideResponseDto requestRide(@Valid @RequestBody RideRequestDto requestDto) {
        return rideService.requestRide(requestDto);
    }

    @PutMapping("/{id}/accept")
    public RideResponseDto acceptRide(@PathVariable("id") Long rideId) {
        return rideService.acceptRide(rideId);
    }

    @PutMapping("/{id}/start")
    public RideResponseDto startRide(@PathVariable("id") Long rideId) {
        return rideService.startRide(rideId);
    }

    @PutMapping("/{id}/complete")
    public RideResponseDto completeRide(@PathVariable("id") Long rideId) {
        return rideService.completeRide(rideId);
    }
}
