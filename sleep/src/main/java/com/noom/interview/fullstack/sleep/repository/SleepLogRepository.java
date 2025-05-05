package com.noom.interview.fullstack.sleep.repository;

import com.noom.interview.fullstack.sleep.entity.SleepLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
     * Retrieves all sleep logs for a user from the past 30 days.
     *
     * @param userId the UUID of the user
     * @param cutoff the date 30 days ago used as the lower bound for filtering logs
     * @return a list of {@link SleepLog} entries from the past 30 days, ordered by sleep date
     */
    @Query("SELECT s FROM SleepLog s WHERE s.userId = :userId AND s.sleepDate >= :cutoff ORDER BY s.sleepDate")
    List<SleepLog> findLast30DaysLogs(@Param("userId") UUID userId, @Param("cutoff") LocalDate cutoff);
}
