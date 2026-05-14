package com.rapido.booking.strategy;

import java.util.List;

import com.rapido.booking.entity.Driver;

public interface DriverMatchingStrategy {

    Driver selectDriver(List<Driver> availableDrivers, double pickupLatitude, double pickupLongitude);
}
