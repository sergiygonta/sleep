package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.dto.SleepLogDto;
import com.noom.interview.fullstack.sleep.entity.SleepLog;
import com.noom.interview.fullstack.sleep.entity.SleepLog.MorningFeeling;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SleepLogServiceTest {

    private SleepLogRepository repository;
    private SleepLogService service;

    @BeforeEach
    void setUp() {
        repository = mock(SleepLogRepository.class);
        service = new SleepLogService(repository);
    }

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

        SleepLog mockSaved = new SleepLog();
        mockSaved.setId(UUID.randomUUID());
        mockSaved.setUserId(userId);
        mockSaved.setSleepDate(dto.getSleepDate());
        mockSaved.setTimeInBedStart(dto.getTimeInBedStart());
        mockSaved.setTimeInBedEnd(dto.getTimeInBedEnd());
        mockSaved.setTotalTimeInBedMinutes(dto.getTotalTimeInBedMinutes());
        mockSaved.setMorningFeeling(SleepLog.MorningFeeling.valueOf(dto.getMorningFeeling()));

        when(repository.save(any(SleepLog.class))).thenReturn(mockSaved);

        SleepLog saved = service.save(dto);

        assertNotNull(saved);
        assertEquals(userId, saved.getUserId());
        verify(repository, times(1)).save(any(SleepLog.class));
    }


    @Test
    void getLastLog_returnsMostRecentLog() {
        UUID userId = UUID.randomUUID();
        SleepLog expected = new SleepLog();
        expected.setUserId(userId);
        expected.setSleepDate(LocalDate.now());
        expected.setMorningFeeling(MorningFeeling.GOOD);

        when(repository.findTopByUserIdOrderBySleepDateDesc(userId))
                .thenReturn(Optional.of(expected));

        Optional<SleepLogDto> result = service.getLastLog(userId);
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getUserId());
    }

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

        when(repository.findLast30DaysLogs(userId)).thenReturn(logs);

        Map<String, Object> stats = service.get30DayStats(userId);
        assertEquals(510.0, stats.get("avg_time_in_bed_minutes"));
        assertTrue(((Map<?, ?>) stats.get("morning_feelings")).containsKey("OK"));
    }
}
