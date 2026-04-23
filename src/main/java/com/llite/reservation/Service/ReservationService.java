package com.llite.reservation.Service;

import com.llite.reservation.Entity.Performance;
import com.llite.reservation.Entity.Reservation;
import com.llite.reservation.Enum.PerformanceStatus;
import com.llite.reservation.Repository.PerformanceRepository;
import com.llite.reservation.Repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final PerformanceRepository performanceRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void reserveSeat(Long performanceId, Long userId) {
//        Performance performance = performanceRepository.findByIdWithPessimisticLock(performanceId);
//
//        if(performance == null) {
//            throw new IllegalArgumentException("존재하지 않는 공연입니다.");
//        }

        Performance performance = performanceRepository.findById(performanceId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));

        if(performance.getStatus() != PerformanceStatus.OPEN) {
            throw new IllegalArgumentException("현재 예매 가능한 상태가 아닙니다.");
        }

        performance.decreaseSeat();

        Reservation reservation = Reservation.builder()
                .performanceId(performanceId)
                .userId(userId).build();

        reservationRepository.save(reservation);
    }
}
