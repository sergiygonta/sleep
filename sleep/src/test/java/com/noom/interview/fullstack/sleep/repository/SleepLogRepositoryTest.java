package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.entity.SleepLog;
import com.noom.interview.fullstack.sleep.entity.SleepLog.MorningFeeling;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SleepLogRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void overrideDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private SleepLogRepository repository;

    @Test
    void testFindTopByUserIdOrderBySleepDateDesc() {
        UUID userId = UUID.randomUUID();

        SleepLog log = new SleepLog();
        log.setId(UUID.randomUUID());
        log.setUserId(userId);
        log.setSleepDate(LocalDate.now());
        log.setTimeInBedStart(LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0)));
        log.setTimeInBedEnd(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(6, 0)));
        log.setTotalTimeInBedMinutes(480);
        log.setMorningFeeling(MorningFeeling.GOOD);

        repository.save(log);

        Optional<SleepLog> result = repository.findTopByUserIdOrderBySleepDateDesc(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
    }

    @Test
    void testFindLast30DaysLogs() {
        UUID userId = UUID.randomUUID();

        for (int i = 0; i < 5; i++) {
            SleepLog log = new SleepLog();
            log.setId(UUID.randomUUID());
            log.setUserId(userId);
            log.setSleepDate(LocalDate.now().minusDays(i));
            log.setTimeInBedStart(LocalDateTime.of(LocalDate.now().minusDays(i), LocalTime.of(22, 0)));
            log.setTimeInBedEnd(LocalDateTime.of(LocalDate.now().minusDays(i).plusDays(1), LocalTime.of(6, 0)));
            log.setTotalTimeInBedMinutes(480);
            log.setMorningFeeling(MorningFeeling.OK);
            repository.save(log);
        }

        List<SleepLog> results = repository.findLast30DaysLogs(userId);
        assertEquals(5, results.size());
    }
}
