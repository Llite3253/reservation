package com.llite.reservation.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class WaitingQueueService {
    private final StringRedisTemplate redisTemplate;

    public WaitingQueueService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String getQueueKey(Long performanceId) {
        return "queue:performance:" + performanceId;
    }

    public void joinQueue(Long performanceId, Long userId) {
        String key = getQueueKey(performanceId);
        Long timestamp = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(key, userId.toString(), timestamp);

        System.out.println("[대기열 진입] 유저 ID: " + userId + " / 진입 시간: " + timestamp);
    }

    public Long getWaitingOrder(Long performanceId, Long userId) {
        String key = getQueueKey(performanceId);

        Long rank = redisTemplate.opsForZSet().rank(key, userId.toString());

        if(rank == null) {
            return -1L;
        }

        return rank;
    }

    private String getActiveKey(Long performanceId) {
        return "active:performance:" + performanceId;
    }

    public void allowEntry(Long performanceId, long count) {
        String waitKey = getQueueKey(performanceId);
        String activeKey = getActiveKey(performanceId);

        Set<String> users = redisTemplate.opsForZSet().range(waitKey, 0, count - 1);

        if(users == null || users.isEmpty()) {
            return;
        }

        redisTemplate.opsForZSet().remove(waitKey, users.toArray());

        redisTemplate.opsForSet().add(activeKey, users.toArray(new String[0]));

        System.out.println("[입장 완료] " + users.size() + "명의 유저가 예매 서버로 진입했습니다.! " + users);
    }

    public boolean isAllowedToEnter(Long performanceId, Long userId) {
        String activeKey = getActiveKey(performanceId);
        Boolean isMember = redisTemplate.opsForSet().isMember(activeKey, userId.toString());
        return isMember != null && isMember;
    }

    public void removeFromActiveQueue(Long performanceId, Long userId) {
        String activeKey = getActiveKey(performanceId);
        redisTemplate.opsForSet().remove(activeKey, userId.toString());
    }

    @Scheduled(fixedDelay = 1000)
    public void scheduleEntry() {
        Long performanceId = 999L;
        long allowCount = 100;

        allowEntry(performanceId, allowCount);
    }


}
