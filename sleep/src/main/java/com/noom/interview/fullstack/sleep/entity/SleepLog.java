package com.noom.interview.fullstack.sleep.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "sleep_logs")
@Data
public class SleepLog {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "sleep_date", nullable = false)
    private LocalDate sleepDate;

    @Column(name = "time_in_bed_start", nullable = false)
    private LocalDateTime timeInBedStart;

    @Column(name = "time_in_bed_end", nullable = false)
    private LocalDateTime timeInBedEnd;

    @Column(name = "total_time_in_bed_minutes", nullable = false)
    private int totalTimeInBedMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "morning_feeling", nullable = false)
    private MorningFeeling morningFeeling;

    public enum MorningFeeling {
        BAD, OK, GOOD
    }
}
