package com.llite.reservation.Repository;

import com.llite.reservation.Entity.Performance;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    //@Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT * FROM Performance WHERE id = :id FOR UPDATE", nativeQuery = true)
    Performance findByIdWithPessimisticLock(@Param("id") Long id);
}
