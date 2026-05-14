package com.rapido.booking.state;

import org.springframework.stereotype.Component;

import com.rapido.booking.entity.TripStatus;
import com.rapido.booking.exception.InvalidTripStateTransitionException;

@Component
public class RequestedTripStateHandler implements TripStateHandler {

    @Override
    public TripStatus currentStatus() {
        return TripStatus.REQUESTED;
    }

    @Override
    public void validateTransition(TripStatus nextStatus) {
        if (nextStatus != TripStatus.ACCEPTED) {
            throw new InvalidTripStateTransitionException("Trip in REQUESTED state can only move to ACCEPTED");
        }
    }
}
