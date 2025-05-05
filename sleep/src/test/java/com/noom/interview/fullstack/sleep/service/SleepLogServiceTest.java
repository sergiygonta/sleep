package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.dto.SleepLogDto;
import com.noom.interview.fullstack.sleep.entity.SleepLog;
import com.noom.interview.fullstack.sleep.entity.SleepLog.MorningFeeling;
import com.noom.interview.fullstack.sleep.mapper.SleepLogMapper;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SleepLogService}.
 */
class SleepLogServiceTest {

    private SleepLogRepository repository;
    private SleepLogMapper mapper;
    private SleepLogService service;

    /**
     * Initializes mocks before each test.
     */
    @BeforeEach
    void setUp() {
        repository = mock(SleepLogRepository.class);
        mapper = mock(SleepLogMapper.class);
        service = new SleepLogService(repository, mapper);
    }

    /**
     * Tests saving a sleep log.
     */
    @Test
    void save_shouldPersistSleepLog() {
        UUID userId = UUID.randomUUID();
        SleepLogDto dto = new SleepLogDto(
                userId,
                LocalDate.now(),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0)),
                LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(6, 30)),
                510,
                "GOOD"
        );

        SleepLog mapped = new SleepLog();
        mapped.setUserId(userId);
        mapped.setSleepDate(dto.getSleepDate());
        mapped.setTimeInBedStart(dto.getTimeInBedStart());
        mapped.setTimeInBedEnd(dto.getTimeInBedEnd());
        mapped.setTotalTimeInBedMinutes(dto.getTotalTimeInBedMinutes());

        SleepLog saved = new SleepLog();
        saved.setId(UUID.randomUUID());
        saved.setUserId(userId);
        saved.setSleepDate(dto.getSleepDate());
        saved.setTimeInBedStart(dto.getTimeInBedStart());
        saved.setTimeInBedEnd(dto.getTimeInBedEnd());
        saved.setTotalTimeInBedMinutes(dto.getTotalTimeInBedMinutes());
        saved.setMorningFeeling(MorningFeeling.GOOD);

        when(mapper.toEntity(dto)).thenReturn(mapped);
        when(repository.save(any(SleepLog.class))).thenReturn(saved);

        SleepLog result = service.save(dto);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        verify(repository).save(any(SleepLog.class));
    }

    /**
     * Tests fetching the most recent sleep log.
     */
    @Test
    void getLastLog_returnsMostRecentLog() {
        UUID userId = UUID.randomUUID();
        SleepLog log = new SleepLog();
        log.setUserId(userId);
        log.setSleepDate(LocalDate.now());
        log.setMorningFeeling(MorningFeeling.GOOD);

        SleepLogDto dto = new SleepLogDto();
        dto.setUserId(userId);

        when(repository.findTopByUserIdOrderBySleepDateDesc(userId)).thenReturn(Optional.of(log));
        when(mapper.toDto(log)).thenReturn(dto);

        Optional<SleepLogDto> result = service.getLastLog(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
    }

    /**
     * Tests the calculation of 30-day average sleep statistics.
     */
    @Test
    void get30DayStats_returnsCorrectStats() {
        UUID userId = UUID.randomUUID();
        List<SleepLog> logs = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            SleepLog log = new SleepLog();
            log.setUserId(userId);
            log.setSleepDate(LocalDate.now().minusDays(i));
            log.setTimeInBedStart(LocalDateTime.of(LocalDate.now().minusDays(i), LocalTime.of(22, 0)));
            log.setTimeInBedEnd(LocalDateTime.of(LocalDate.now().minusDays(i).plusDays(1), LocalTime.of(6, 30)));
            log.setTotalTimeInBedMinutes(510);
            log.setMorningFeeling(MorningFeeling.OK);
            logs.add(log);
        }

        when(repository.findLast30DaysLogs(eq(userId), any())).thenReturn(logs);

        Map<String, Object> stats = service.get30DayStats(userId);
        assertEquals(510.0, stats.get("avg_time_in_bed_minutes"));
        assertTrue(((Map<?, ?>) stats.get("morning_feelings")).containsKey("OK"));
    }
}
