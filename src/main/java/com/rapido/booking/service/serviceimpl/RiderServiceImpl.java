package com.rapido.booking.service.serviceimpl;

import org.springframework.stereotype.Service;

import com.rapido.booking.dto.RiderRequestDto;
import com.rapido.booking.dto.RiderResponseDto;
import com.rapido.booking.entity.Rider;
import com.rapido.booking.exception.ResourceNotFoundException;
import com.rapido.booking.repository.RiderRepository;
import com.rapido.booking.service.RiderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RiderServiceImpl implements RiderService {

    private final RiderRepository riderRepository;

    @Override
    public RiderResponseDto createRider(RiderRequestDto requestDto) {
        Rider rider = riderRepository.save(Rider.builder().name(requestDto.getName()).build());
        log.info("Created rider with id={}", rider.getId());
        return RiderResponseDto.builder().id(rider.getId()).name(rider.getName()).build();
    }

    @Override
    public Rider getExistingRider(Long riderId) {
        return riderRepository.findById(riderId)
                .orElseThrow(() -> new ResourceNotFoundException("Rider not found for id: " + riderId));
    }
}
