package com.rapido.booking.service.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rapido.booking.dto.RideRequestDto;
import com.rapido.booking.dto.RideResponseDto;
import com.rapido.booking.entity.Driver;
import com.rapido.booking.entity.Rider;
import com.rapido.booking.entity.Trip;
import com.rapido.booking.entity.TripStatus;
import com.rapido.booking.exception.ResourceNotFoundException;
import com.rapido.booking.repository.DriverRepository;
import com.rapido.booking.repository.TripRepository;
import com.rapido.booking.service.RideService;
import com.rapido.booking.service.RiderService;
import com.rapido.booking.state.TripStateMachine;
import com.rapido.booking.strategy.DriverMatchingStrategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RideServiceImpl implements RideService {

    private final RiderService riderService;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;
    private final DriverMatchingStrategy driverMatchingStrategy;
    private final TripStateMachine tripStateMachine;

    @Override
    @Transactional
    public RideResponseDto requestRide(RideRequestDto requestDto) {
        Rider rider = riderService.getExistingRider(requestDto.getRiderId());

        // Lock all currently available drivers inside the same transaction so parallel requests
        // cannot assign the same driver more than once.
        List<Driver> availableDrivers = driverRepository.findAvailableDriversForUpdate();
        Driver assignedDriver = driverMatchingStrategy.selectDriver(
                availableDrivers,
                requestDto.getPickupLatitude(),
                requestDto.getPickupLongitude());

        assignedDriver.setAvailability(false);
        Trip trip = tripRepository.save(Trip.builder()
                .rider(rider)
                .driver(assignedDriver)
                .status(TripStatus.REQUESTED)
                .pickupLocation(requestDto.getPickupLocation())
                .build());

        log.info("Created trip id={} for rider={} and driver={}", trip.getId(), rider.getId(), assignedDriver.getId());
        return toResponse(trip);
    }

    @Override
    @Transactional
    public RideResponseDto acceptRide(Long tripId) {
        Trip trip = getExistingTrip(tripId);
        updateTripStatus(trip, TripStatus.ACCEPTED);
        log.info("Trip id={} accepted", tripId);
        return toResponse(trip);
    }

    @Override
    @Transactional
    public RideResponseDto startRide(Long tripId) {
        Trip trip = getExistingTrip(tripId);
        updateTripStatus(trip, TripStatus.STARTED);
        log.info("Trip id={} started", tripId);
        return toResponse(trip);
    }

    @Override
    @Transactional
    public RideResponseDto completeRide(Long tripId) {
        Trip trip = getExistingTrip(tripId);
        updateTripStatus(trip, TripStatus.COMPLETED);
        trip.getDriver().setAvailability(true);
        log.info("Trip id={} completed and driver id={} is available again", tripId, trip.getDriver().getId());
        return toResponse(trip);
    }

    private Trip getExistingTrip(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found for id: " + tripId));
    }

    private void updateTripStatus(Trip trip, TripStatus targetStatus) {
        tripStateMachine.validate(trip.getStatus(), targetStatus);
        trip.setStatus(targetStatus);
    }

    private RideResponseDto toResponse(Trip trip) {
        return RideResponseDto.builder()
                .tripId(trip.getId())
                .riderId(trip.getRider().getId())
                .driverId(trip.getDriver().getId())
                .driverName(trip.getDriver().getName())
                .status(trip.getStatus())
                .pickupLocation(trip.getPickupLocation())
                .createdAt(trip.getCreatedAt())
                .build();
    }
}
