package com.rapido.booking.state;

import org.springframework.stereotype.Component;

import com.rapido.booking.entity.TripStatus;
import com.rapido.booking.exception.InvalidTripStateTransitionException;

@Component
public class CompletedTripStateHandler implements TripStateHandler {

    @Override
    public TripStatus currentStatus() {
        return TripStatus.COMPLETED;
    }

    @Override
    public void validateTransition(TripStatus nextStatus) {
        throw new InvalidTripStateTransitionException("Completed trips do not allow further state transitions");
    }
}
