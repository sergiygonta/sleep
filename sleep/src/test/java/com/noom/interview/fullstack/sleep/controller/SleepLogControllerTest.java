package com.noom.interview.fullstack.sleep.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noom.interview.fullstack.sleep.dto.SleepLogDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link SleepLogController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class SleepLogControllerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;

    /**
     * Initializes test data before each test method.
     */
    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
    }

    /**
     * Tests the sleep log creation endpoint.
     */
    @Test
    void testLogSleep() throws Exception {
        SleepLogDto dto = new SleepLogDto(
                userId,
                LocalDate.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 30)),
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(6, 0)),
                450,
                "OK"
        );

        mockMvc.perform(post("/api/sleep")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    /**
     * Tests fetching the most recent sleep log.
     */
    @Test
    void testGetLastSleepLog() throws Exception {
        testLogSleep();

        mockMvc.perform(get("/api/sleep/{userId}/latest", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    /**
     * Tests the 30-day sleep stats endpoint.
     */
    @Test
    void testGet30DayStats() throws Exception {
        testLogSleep();

        mockMvc.perform(get("/api/sleep/{userId}/30-day-avg", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.avg_time_in_bed_minutes").exists());
    }
}
