package com.rapido.booking.service.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.rapido.booking.dto.DriverRequestDto;
import com.rapido.booking.dto.DriverResponseDto;
import com.rapido.booking.entity.Driver;
import com.rapido.booking.repository.DriverRepository;
import com.rapido.booking.service.DriverService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    public DriverResponseDto createDriver(DriverRequestDto requestDto) {
        Driver driver = driverRepository.save(Driver.builder()
                .name(requestDto.getName())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())
                .availability(requestDto.getAvailability())
                .rating(requestDto.getRating())
                .build());
        log.info("Created driver with id={} availability={}", driver.getId(), driver.getAvailability());
        return toResponse(driver);
    }

    @Override
    public List<DriverResponseDto> getAvailableDrivers() {
        return driverRepository.findByAvailabilityTrueOrderByRatingDesc().stream().map(this::toResponse).toList();
    }

    private DriverResponseDto toResponse(Driver driver) {
        return DriverResponseDto.builder()
                .id(driver.getId())
                .name(driver.getName())
                .latitude(driver.getLatitude())
                .longitude(driver.getLongitude())
                .availability(driver.getAvailability())
                .rating(driver.getRating())
                .build();
    }
}
