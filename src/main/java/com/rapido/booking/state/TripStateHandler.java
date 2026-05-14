package com.rapido.booking.state;

import com.rapido.booking.entity.TripStatus;

public interface TripStateHandler {

    TripStatus currentStatus();

    void validateTransition(TripStatus nextStatus);
}
