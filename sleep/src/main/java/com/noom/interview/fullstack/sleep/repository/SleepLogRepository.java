package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.entity.SleepLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Repository for accessing SleepLog entities.
 */
@Repository
public interface SleepLogRepository extends JpaRepository<SleepLog, UUID> {

    /**
     * Find the most recent sleep log for a given user.
     *
     * @param userId UUID of the user.
     * @return Optional containing the most recent SleepLog.
     */
    Optional<SleepLog> findTopByUserIdOrderBySleepDateDesc(UUID userId);

    /**
     * Find all sleep logs from the past 30 days for a user.
     *
     * @param userId UUID of the user.
     * @return List of SleepLogs.
     */
    @Query("SELECT s FROM SleepLog s WHERE s.userId = :userId AND s.sleepDate >= CURRENT_DATE - 30 ORDER BY s.sleepDate")
    List<SleepLog> findLast30DaysLogs(UUID userId);
}
