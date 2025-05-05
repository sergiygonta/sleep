package com.noom.interview.fullstack.sleep.service;

import com.noom.interview.fullstack.sleep.dto.SleepLogDto;
import com.noom.interview.fullstack.sleep.entity.SleepLog;
import com.noom.interview.fullstack.sleep.mapper.SleepLogMapper;
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
    private final SleepLogMapper mapper = SleepLogMapper.INSTANCE;

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
        SleepLog log = mapper.toEntity(dto);
        log.setId(UUID.randomUUID()); // Manually setting ID
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
        if (logs.isEmpty()) {
            return Collections.emptyMap();
        }

        return buildSleepStats(logs);
    }

    /**
     * Retrieves the most recent sleep log for a user.
     *
     * @param userId UUID of the user.
     * @return Optional of SleepLogDto.
     */
    public Optional<SleepLogDto> getLastLog(UUID userId) {
        return repository.findTopByUserIdOrderBySleepDateDesc(userId)
                .map(mapper::toDto);
    }


    private Map<String, Object> buildSleepStats(List<SleepLog> logs) {
        LocalTime avgStart = averageLocalTime(logs.stream().map(SleepLog::getTimeInBedStart));
        LocalTime avgEnd = averageLocalTime(logs.stream().map(SleepLog::getTimeInBedEnd));
        Map<String, Long> morningFeelings = countMorningFeelings(logs);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("date_range", formatDateRange(logs));
        result.put("avg_time_in_bed_minutes", calculateAverageTimeInBedMinutes(logs));
        if (avgStart != null) result.put("avg_time_in_bed_start", avgStart);
        if (avgEnd != null) result.put("avg_time_in_bed_end", avgEnd);
        result.put("morning_feelings", morningFeelings);
        return result;
    }

    private double calculateAverageTimeInBedMinutes(List<SleepLog> logs) {
        return logs.stream()
                .mapToInt(SleepLog::getTotalTimeInBedMinutes)
                .average()
                .orElse(0);
    }

    private LocalTime averageLocalTime(Stream<LocalDateTime> dateTimeStream) {
        List<LocalTime> times = dateTimeStream
                .map(LocalDateTime::toLocalTime)
                .collect(Collectors.toList());

        int totalSeconds = times.stream()
                .mapToInt(LocalTime::toSecondOfDay)
                .sum();

        return times.isEmpty() ? null : LocalTime.ofSecondOfDay(totalSeconds / times.size());
    }

    private Map<String, Long> countMorningFeelings(List<SleepLog> logs) {
        return logs.stream()
                .collect(Collectors.groupingBy(
                        l -> l.getMorningFeeling().name(),
                        Collectors.counting()
                ));
    }

    private String formatDateRange(List<SleepLog> logs) {
        return logs.get(0).getSleepDate() + " to " + logs.get(logs.size() - 1).getSleepDate();
    }
}
