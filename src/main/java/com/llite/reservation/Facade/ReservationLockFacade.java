package com.llite.reservation.Facade;

import com.llite.reservation.Service.ReservationService;
import com.llite.reservation.Service.WaitingQueueService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ReservationLockFacade {
    private final RedissonClient redissonClient;
    private final ReservationService reservationService;
    private final WaitingQueueService waitingQueueService;

    public ReservationLockFacade(RedissonClient redissonClient, ReservationService reservationService, WaitingQueueService waitingQueueService) {
        this.redissonClient = redissonClient;
        this.reservationService = reservationService;
        this.waitingQueueService = waitingQueueService;
    }

    public void reserveSeatWithRedisLock(Long performanceId, Long userId) {
        if(!waitingQueueService.isAllowedToEnter(performanceId, userId)) {
            System.out.println("[접근 차단] 아직 대기열 순서가 되지 않았거나, 비정상적인 접근입니다. 유저 ID: " + userId);
            return;
        }

        RLock lock = redissonClient.getLock("performance_lock:" + performanceId);

        try {
            //boolean available = lock.tryLock(10, 3, TimeUnit.SECONDS);
            boolean available = lock.tryLock(60, TimeUnit.SECONDS);

            if(!available) {
                System.out.println("락 획득 실패 (대기 시간 초과): 유저 ID " + userId);
                return;
            }

            reservationService.reserveSeat(performanceId, userId);
        } catch (InterruptedException e) {
            throw new RuntimeException("락 획득 중 인터럽트 발생", e);
        } finally {
            if(lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            waitingQueueService.removeFromActiveQueue(performanceId, userId);
        }
    }
}
