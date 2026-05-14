package com.rapido.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.rapido.booking.entity.Driver;

import jakarta.persistence.LockModeType;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByAvailabilityTrueOrderByRatingDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Driver d where d.availability = true")
    List<Driver> findAvailableDriversForUpdate();
}
