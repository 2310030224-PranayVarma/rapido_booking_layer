package com.rapido.booking;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapido.booking.dto.RideRequestDto;
import com.rapido.booking.entity.Driver;
import com.rapido.booking.entity.Rider;
import com.rapido.booking.repository.DriverRepository;
import com.rapido.booking.repository.RiderRepository;
import com.rapido.booking.repository.TripRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class RideControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TripRepository tripRepository;

    private Rider rider;
    private Driver nearestDriver;
    private Driver fartherDriver;

    @BeforeEach
    void setUp() {
        tripRepository.deleteAll();
        driverRepository.deleteAll();
        riderRepository.deleteAll();

        rider = riderRepository.save(Rider.builder().name("Pranay").build());
        nearestDriver = driverRepository.save(Driver.builder()
                .name("Nearest Driver")
                .latitude(12.9717)
                .longitude(77.5940)
                .availability(true)
                .rating(4.7)
                .build());
        fartherDriver = driverRepository.save(Driver.builder()
                .name("Farther Driver")
                .latitude(13.1000)
                .longitude(77.7000)
                .availability(true)
                .rating(4.9)
                .build());
    }

    @Test
    void requestRide_assignsNearestDriverAndRemovesItFromAvailableList() throws Exception {
        RideRequestDto requestDto = new RideRequestDto();
        requestDto.setRiderId(rider.getId());
        requestDto.setPickupLocation("Indiranagar");
        requestDto.setPickupLatitude(12.9718);
        requestDto.setPickupLongitude(77.5941);

        mockMvc.perform(post("/rides/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.driverId").value(nearestDriver.getId()))
                .andExpect(jsonPath("$.status").value("REQUESTED"));

        mockMvc.perform(get("/drivers/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(fartherDriver.getId()));
    }

    @Test
    void rideLifecycle_allowsValidTransitionsAndReleasesDriverOnCompletion() throws Exception {
        RideRequestDto requestDto = new RideRequestDto();
        requestDto.setRiderId(rider.getId());
        requestDto.setPickupLocation("Koramangala");
        requestDto.setPickupLatitude(12.9352);
        requestDto.setPickupLongitude(77.6245);

        String response = mockMvc.perform(post("/rides/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tripId = objectMapper.readTree(response).get("tripId").asLong();

        mockMvc.perform(put("/rides/{id}/accept", tripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        mockMvc.perform(put("/rides/{id}/start", tripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("STARTED"));

        mockMvc.perform(put("/rides/{id}/complete", tripId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        mockMvc.perform(get("/drivers/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void invalidTransition_returnsBadRequest() throws Exception {
        RideRequestDto requestDto = new RideRequestDto();
        requestDto.setRiderId(rider.getId());
        requestDto.setPickupLocation("HSR Layout");
        requestDto.setPickupLatitude(12.9116);
        requestDto.setPickupLongitude(77.6474);

        String response = mockMvc.perform(post("/rides/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long tripId = objectMapper.readTree(response).get("tripId").asLong();

        mockMvc.perform(put("/rides/{id}/start", tripId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Trip in REQUESTED state can only move to ACCEPTED"));
    }
}
