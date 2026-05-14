package com.rapido.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.rapido.booking.dto.RideRequestDto;
import com.rapido.booking.entity.Driver;
import com.rapido.booking.entity.Rider;
import com.rapido.booking.exception.NoAvailableDriverException;
import com.rapido.booking.repository.DriverRepository;
import com.rapido.booking.repository.RiderRepository;
import com.rapido.booking.repository.TripRepository;
import com.rapido.booking.service.RideService;

@SpringBootTest
@ActiveProfiles("test")
class RideServiceConcurrencyTest {

    @Autowired
    private RideService rideService;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TripRepository tripRepository;

    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        tripRepository.deleteAll();
        driverRepository.deleteAll();
        riderRepository.deleteAll();
        executorService = Executors.newFixedThreadPool(2);
    }

    @AfterEach
    void tearDown() {
        executorService.shutdownNow();
    }

    @Test
    void concurrentRequests_doNotAssignSameDriverTwice() throws Exception {
        Rider riderOne = riderRepository.save(Rider.builder().name("Rider One").build());
        Rider riderTwo = riderRepository.save(Rider.builder().name("Rider Two").build());
        Driver onlyDriver = driverRepository.save(Driver.builder()
                .name("Solo Driver")
                .latitude(12.9716)
                .longitude(77.5946)
                .availability(true)
                .rating(4.8)
                .build());

        CountDownLatch startLatch = new CountDownLatch(1);
        List<Callable<Boolean>> requests = List.of(
                rideRequestTask(startLatch, riderOne.getId()),
                rideRequestTask(startLatch, riderTwo.getId())
        );

        List<Future<Boolean>> futures = new ArrayList<>();
        requests.forEach(task -> futures.add(executorService.submit(task)));
        startLatch.countDown();

        int successCount = 0;
        int noDriverCount = 0;
        for (Future<Boolean> future : futures) {
            try {
                if (future.get()) {
                    successCount++;
                }
            } catch (ExecutionException executionException) {
                if (executionException.getCause() instanceof NoAvailableDriverException) {
                    noDriverCount++;
                } else {
                    throw executionException;
                }
            }
        }

        assertEquals(1, successCount);
        assertEquals(1, noDriverCount);
        assertEquals(1, tripRepository.count());
        assertFalse(driverRepository.findById(onlyDriver.getId()).orElseThrow().getAvailability());
    }

    private Callable<Boolean> rideRequestTask(CountDownLatch startLatch, Long riderId) {
        return () -> {
            startLatch.await();
            RideRequestDto requestDto = new RideRequestDto();
            requestDto.setRiderId(riderId);
            requestDto.setPickupLocation("MG Road");
            requestDto.setPickupLatitude(12.9750);
            requestDto.setPickupLongitude(77.6050);
            assertTrue(rideService.requestRide(requestDto).getTripId() > 0);
            return true;
        };
    }
}
