package com.llite.reservation.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 604800)
@Getter
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String id;

    private String refreshToken;

    public void updateRefreshToken(String token) {
        this.refreshToken = token;
    }
}
