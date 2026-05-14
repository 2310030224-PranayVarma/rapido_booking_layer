package com.rapido.booking.state;

import org.springframework.stereotype.Component;

import com.rapido.booking.entity.TripStatus;
import com.rapido.booking.exception.InvalidTripStateTransitionException;

@Component
public class AcceptedTripStateHandler implements TripStateHandler {

    @Override
    public TripStatus currentStatus() {
        return TripStatus.ACCEPTED;
    }

    @Override
    public void validateTransition(TripStatus nextStatus) {
        if (nextStatus != TripStatus.STARTED) {
            throw new InvalidTripStateTransitionException("Trip in ACCEPTED state can only move to STARTED");
        }
    }
}
