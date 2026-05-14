package com.rapido.booking.state;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.rapido.booking.entity.TripStatus;
import com.rapido.booking.exception.InvalidTripStateTransitionException;

@Component
public class TripStateMachine {

    private final Map<TripStatus, TripStateHandler> handlers = new EnumMap<>(TripStatus.class);

    public TripStateMachine(List<TripStateHandler> stateHandlers) {
        stateHandlers.forEach(handler -> handlers.put(handler.currentStatus(), handler));
    }

    public void validate(TripStatus currentStatus, TripStatus nextStatus) {
        TripStateHandler handler = handlers.get(currentStatus);
        if (handler == null) {
            throw new InvalidTripStateTransitionException("Unsupported current trip state: " + currentStatus);
        }
        handler.validateTransition(nextStatus);
    }
}
