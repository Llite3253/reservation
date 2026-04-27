package com.llite.reservation;

import com.llite.reservation.Service.WaitingQueueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class WaitingQueueServiceTest {
    @Autowired
    private WaitingQueueService waitingQueueService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    @DisplayName("n명의 유저가 순서대로 대기열에 집입하고 순위를 확인한다")
    void queue_order_test() throws InterruptedException {
        Long performanceId = 999L;
        String key = "queue:performance:" + performanceId;

        redisTemplate.delete(key);

        for(long i=1; i<=5; i++) {
            waitingQueueService.joinQueue(performanceId, i);
            Thread.sleep(5);
        }

        System.out.println("====================");

        Long rankUser1 = waitingQueueService.getWaitingOrder(performanceId, 1L);
        System.out.println("1번 유저의 앞 대기자 수: " + rankUser1 + "명");

        Long rankUser3 = waitingQueueService.getWaitingOrder(performanceId, 3L);
        System.out.println("3번 유저의 앞 대기자 수: " + rankUser3 + "명");

        assertThat(rankUser1).isEqualTo(0);
        assertThat(rankUser3).isEqualTo(2);
    }
}
