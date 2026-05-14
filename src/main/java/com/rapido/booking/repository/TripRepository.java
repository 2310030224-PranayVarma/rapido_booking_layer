package com.rapido.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rapido.booking.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Long> {
}
