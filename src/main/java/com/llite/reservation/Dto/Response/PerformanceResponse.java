package com.llite.reservation.Dto.Response;

import com.llite.reservation.Enum.PerformanceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PerformanceResponse {
    private Long id;
    private String title;
    private String venue;
    private LocalDateTime startAt;
    private LocalDateTime reservationStartAt;
    private int price;
    private int totalSeats;
    private PerformanceStatus status;
}
