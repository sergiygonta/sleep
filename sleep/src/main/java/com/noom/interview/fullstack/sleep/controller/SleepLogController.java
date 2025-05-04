package com.noom.interview.fullstack.sleep.controller;

import com.noom.interview.fullstack.sleep.dto.SleepLogDto;
import com.noom.interview.fullstack.sleep.service.SleepLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Sleep Log", description = "Operations related to sleep logging")
@RestController
@RequestMapping("/api/sleep")
public class SleepLogController {

    private final SleepLogService service;

    @Autowired
    public SleepLogController(SleepLogService service) {
        this.service = service;
    }

    @Operation(summary = "Log new sleep entry")
    @PostMapping
    public ResponseEntity<Object> logSleep(@RequestBody SleepLogDto dto) {
        return new ResponseEntity<>(service.save(dto), HttpStatus.OK);
    }

    @Operation(summary = "Get latest sleep log for user")
    @GetMapping("/{userId}/latest")
    public ResponseEntity<SleepLogDto> getLast(@PathVariable UUID userId) {
        return service.getLastLog(userId)
                .map(log -> new ResponseEntity<>(log, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Get 30-day sleep stats")
    @GetMapping("/{userId}/30-day-avg")
    public ResponseEntity<Object> getStats(@PathVariable UUID userId) {
        return new ResponseEntity<>(service.get30DayStats(userId), HttpStatus.OK);
    }
}
