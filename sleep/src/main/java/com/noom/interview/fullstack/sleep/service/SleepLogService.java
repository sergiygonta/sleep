package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.dto.SleepLogDto;
import com.noom.interview.fullstack.sleep.entity.SleepLog;
import com.noom.interview.fullstack.sleep.repository.SleepLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service layer handling business logic related to sleep logs.
 */
@Service
public class SleepLogService {

    private final SleepLogRepository repository;

    public SleepLogService(SleepLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Saves a new sleep log based on the given DTO.
     *
     * @param dto Data transfer object containing sleep log details.
     * @return Saved SleepLog entity.
     */
    public SleepLog save(SleepLogDto dto) {
        SleepLog log = new SleepLog();
        log.setId(UUID.randomUUID());
        log.setUserId(dto.userId);
        log.setSleepDate(dto.sleepDate);
        log.setTimeInBedStart(dto.timeInBedStart);
        log.setTimeInBedEnd(dto.timeInBedEnd);
        log.setTotalTimeInBedMinutes(dto.totalTimeInBedMinutes);
        log.setMorningFeeling(SleepLog.MorningFeeling.valueOf(dto.morningFeeling.toUpperCase()));
        return repository.save(log);
    }

    /**
     * Computes 30-day sleep statistics for a user.
     *
     * @param userId UUID of the user.
     * @return Map of aggregated statistics.
     */
    public Map<String, Object> get30DayStats(UUID userId) {
        List<SleepLog> logs = repository.findLast30DaysLogs(userId);
        if (logs.isEmpty()) return Map.of();

        double avgMinutes = logs.stream().mapToInt(SleepLog::getTotalTimeInBedMinutes).average().orElse(0);
        LocalTime avgStart = averageTime(logs.stream().map(SleepLog::getTimeInBedStart));
        LocalTime avgEnd = averageTime(logs.stream().map(SleepLog::getTimeInBedEnd));

        Map<String, Long> feelingFrequencies = logs.stream()
                .collect(Collectors.groupingBy(l -> l.getMorningFeeling().name(), Collectors.counting()));

        return Map.of(
                "date_range", logs.get(0).getSleepDate() + " to " + logs.get(logs.size() - 1).getSleepDate(),
                "avg_time_in_bed_minutes", avgMinutes,
                "avg_time_in_bed_start", avgStart,
                "avg_time_in_bed_end", avgEnd,
                "morning_feelings", feelingFrequencies
        );
    }

    /**
     * Averages a stream of LocalDateTime values as LocalTime.
     *
     * @param times Stream of LocalDateTime.
     * @return Averaged LocalTime.
     */
    private LocalTime averageTime(Stream<LocalDateTime> times) {
        List<LocalTime> list = times.map(LocalDateTime::toLocalTime).collect(Collectors.toList());
        int totalSeconds = list.stream().mapToInt(LocalTime::toSecondOfDay).sum();
        return LocalTime.ofSecondOfDay(totalSeconds / list.size());
    }

    /**
     * Retrieves the most recent sleep log for a user.
     *
     * @param userId UUID of the user.
     * @return Optional of SleepLogDto.
     */
    public Optional<SleepLogDto> getLastLog(UUID userId) {
        return repository.findTopByUserIdOrderBySleepDateDesc(userId)
                .map(this::toDto);
    }

    /**
     * Converts a SleepLog entity to its DTO representation.
     *
     * @param log SleepLog entity.
     * @return SleepLogDto.
     */
    private SleepLogDto toDto(SleepLog log) {
        return new SleepLogDto(
                log.getUserId(),
                log.getSleepDate(),
                log.getTimeInBedStart(),
                log.getTimeInBedEnd(),
                log.getTotalTimeInBedMinutes(),
                log.getMorningFeeling().name()
        );
    }
}
