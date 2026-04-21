package com.llite.reservation.Entity;

import com.llite.reservation.Enum.PerformanceStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 공연 제목
    @Column(nullable = false)
    private String title;

    // 공연 상세 설명
    private String description;
    // 공연 장소
    private String venue;

    // 공연 시작 시간
    @Column(nullable = false)
    private LocalDateTime startAt;

    // 예매 시작 일시
    @Column(nullable = false)
    private LocalDateTime reservationStartAt;

    // 가격
    private int price;
    // 총 좌석수
    private int totalSeats;

    // 공연 상태
    @Enumerated(EnumType.STRING)
    private PerformanceStatus status;

//    @Version
//    private Long version;

    @Builder
    public Performance(String title, String description, String venue, LocalDateTime startAt, LocalDateTime reservationStartAt,
                       int price, int totalSeats, PerformanceStatus status) {
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.startAt = startAt;
        this.reservationStartAt = reservationStartAt;
        this.price = price;
        this.totalSeats = totalSeats;
        this.status = status;
    }

    public void decreaseSeat() {
        if(this.totalSeats <= 0) {
            throw new IllegalArgumentException("이미 모든 좌석이 매진되었습니다.");
        }
        this.totalSeats--;
    }
}
