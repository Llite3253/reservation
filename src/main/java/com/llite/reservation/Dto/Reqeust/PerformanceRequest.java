package com.llite.reservation.Dto.Reqeust;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PerformanceRequest {
    private String title;
    private String description;
    private String venue;
    private LocalDateTime startAt;
    private LocalDateTime reservationStartAt;
    private int price;
    private int totalSeats;
}
