package com.llite.reservation.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long performanceId;
    private Long userId;

    @Builder
    private Reservation(Long performanceId, Long userId) {
        this.performanceId = performanceId;
        this.userId = userId;
    }
}
