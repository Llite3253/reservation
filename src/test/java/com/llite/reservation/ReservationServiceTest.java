package com.llite.reservation;

import com.llite.reservation.Entity.Performance;
import com.llite.reservation.Enum.PerformanceStatus;
import com.llite.reservation.Facade.ReservationLockFacade;
import com.llite.reservation.Repository.PerformanceRepository;
import com.llite.reservation.Repository.ReservationRepository;
import com.llite.reservation.Service.ReservationService;
import com.llite.reservation.Service.WaitingQueueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

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

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WaitingQueueService waitingQueueService;

    @Test
    @DisplayName("n명이 동시에 예매를 시도")
    void concurrent_reservation_test() throws InterruptedException {
        Performance performance = performanceRepository.save(Performance.builder()
                .title("아이돌 콘서트")
                .description("아이돌 콘서트입니다.")
                .venue("서울")
                .startAt(LocalDateTime.now()) // 테스트용
                .reservationStartAt(LocalDateTime.now()) // 테스트용
                .price(1000)
                .totalSeats(10000) // 좌석 수
                .status(PerformanceStatus.OPEN)
                .build());

        Long performanceId = performance.getId();

        redisTemplate.delete("queue:performance:" + performanceId);
        redisTemplate.delete("active:performance:" + performanceId);

        int threadCount = 10000;
        //ExecutorService executorService = Executors.newFixedThreadPool(32);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i=0; i<threadCount; i++) {
            long userId = i;
            executorService.submit(() -> {
                try {
                    waitingQueueService.joinQueue(performanceId, userId);

                    while (!waitingQueueService.isAllowedToEnter(performanceId, userId)) {
                        Thread.sleep(1000);
                    }

                    //reservationService.reserveSeat(performanceId, userId);
                    reservationLockFacade.reserveSeatWithRedisLock(performanceId, userId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    //System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 실제로는 @Scheduled가 작동하지만 테스트 환경에서는 메인 스레드가 직접 문을 열어줘야 함.
        while(latch.getCount() > 0) {
            waitingQueueService.allowEntry(performanceId, 20);
            System.out.println("스케줄러 작동 중.. 남은 대기열 작업수: " + latch.getCount());
            Thread.sleep(200);
        }

        latch.await();

        Performance updatedPerformance = performanceRepository.findById(performanceId).orElseThrow();
        long reservationCount = reservationRepository.count();

        System.out.println("총 예매 시도 인원: " + threadCount + " 명");
        System.out.println("남은 좌석 수: " + updatedPerformance.getTotalSeats());
        System.out.println("실제 생성된 예매 내역 수: " + reservationCount);

        assertThat(reservationCount).isEqualTo(10000);
        assertThat(updatedPerformance.getTotalSeats()).isEqualTo(0);
    }
}
