package com.rapido.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rapido.booking.entity.Rider;

public interface RiderRepository extends JpaRepository<Rider, Long> {
}
