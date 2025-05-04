package com.noom.interview.fullstack.sleep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SleepLogDto {
    public UUID userId;
    public LocalDate sleepDate;
    public LocalDateTime timeInBedStart;
    public LocalDateTime timeInBedEnd;
    public int totalTimeInBedMinutes;
    public String morningFeeling;
}
