package com.rapido.booking.strategy;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.rapido.booking.entity.Driver;
import com.rapido.booking.exception.NoAvailableDriverException;

@Component
public class NearestDriverMatchingStrategy implements DriverMatchingStrategy {

    @Override
    public Driver selectDriver(List<Driver> availableDrivers, double pickupLatitude, double pickupLongitude) {
        return availableDrivers.stream()
                .min(Comparator.comparingDouble(driver -> distanceSquared(driver, pickupLatitude, pickupLongitude)))
                .orElseThrow(() -> new NoAvailableDriverException("No available drivers found for the requested ride"));
    }

    private double distanceSquared(Driver driver, double pickupLatitude, double pickupLongitude) {
        double latDiff = driver.getLatitude() - pickupLatitude;
        double lonDiff = driver.getLongitude() - pickupLongitude;
        return (latDiff * latDiff) + (lonDiff * lonDiff);
    }
}
