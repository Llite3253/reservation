package com.llite.reservation;

import com.llite.reservation.Entity.Performance;
import com.llite.reservation.Enum.PerformanceStatus;
import com.llite.reservation.Facade.ReservationLockFacade;
import com.llite.reservation.Repository.PerformanceRepository;
import com.llite.reservation.Repository.ReservationRepository;
import com.llite.reservation.Service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationServiceTest {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationLockFacade reservationLockFacade;

    @Test
    @DisplayName("100명이 동시에 예매를 시도")
    void concurrent_reservation_test() throws InterruptedException {
        Performance performance = performanceRepository.save(Performance.builder()
                .title("아이돌 콘서트")
                .description("아이돌 콘서트입니다.")
                .venue("서울")
                .startAt(LocalDateTime.now()) // 테스트용
                .reservationStartAt(LocalDateTime.now()) // 테스트용
                .price(1000)
                .totalSeats(1000) // 좌석 수
                .status(PerformanceStatus.OPEN)
                .build());

        Long performanceId = performance.getId();

        int threadCount = 100000;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i=0; i<threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    //reservationService.reserveSeat(performanceId, userId);
                    reservationLockFacade.reserveSeatWithRedisLock(performanceId, userId);
                } catch (Exception e) {
                    //System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Performance updatedPerformance = performanceRepository.findById(performanceId).orElseThrow();
        long reservationCount = reservationRepository.count();

        System.out.println("총 예매 시도 인원: " + threadCount + " 명");
        System.out.println("남은 좌석 수: " + updatedPerformance.getTotalSeats());
        System.out.println("실제 생성된 예매 내역 수: " + reservationCount);

        assertThat(reservationCount).isEqualTo(1000);
        assertThat(updatedPerformance.getTotalSeats()).isEqualTo(0);
    }
}
