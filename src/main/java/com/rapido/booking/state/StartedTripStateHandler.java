package com.rapido.booking.state;

import org.springframework.stereotype.Component;

import com.rapido.booking.entity.TripStatus;
import com.rapido.booking.exception.InvalidTripStateTransitionException;

@Component
public class StartedTripStateHandler implements TripStateHandler {

    @Override
    public TripStatus currentStatus() {
        return TripStatus.STARTED;
    }

    @Override
    public void validateTransition(TripStatus nextStatus) {
        if (nextStatus != TripStatus.COMPLETED) {
            throw new InvalidTripStateTransitionException("Trip in STARTED state can only move to COMPLETED");
        }
    }
}
