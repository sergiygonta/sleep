package com.noom.interview.fullstack.sleep.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SleepLogDto {
    @NotNull
    private UUID userId;
    @NotNull private LocalDate sleepDate;
    @NotNull private LocalDateTime timeInBedStart;
    @NotNull private LocalDateTime timeInBedEnd;
    @Min(1) private int totalTimeInBedMinutes;
    @Pattern(regexp = "BAD|OK|GOOD") private String morningFeeling;
}
