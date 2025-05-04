package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.entity.SleepLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface SleepLogRepository extends JpaRepository<SleepLog, UUID> {
    Optional<SleepLog> findTopByUserIdOrderBySleepDateDesc(UUID userId);

    @Query("SELECT s FROM SleepLog s WHERE s.userId = :userId AND s.sleepDate >= CURRENT_DATE - 30 ORDER BY s.sleepDate")
    List<SleepLog> findLast30DaysLogs(UUID userId);
}
