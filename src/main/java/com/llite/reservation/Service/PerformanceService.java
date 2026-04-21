package com.llite.reservation.Service;

import com.llite.reservation.Dto.Reqeust.PerformanceRequest;
import com.llite.reservation.Dto.Response.PerformanceResponse;
import com.llite.reservation.Entity.Performance;
import com.llite.reservation.Repository.PerformanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceService {
    private final PerformanceRepository performanceRepository;

    @Transactional
    public void createPerformance(PerformanceRequest request) {
        performanceRepository.save(Performance.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .venue(request.getVenue())
                .startAt(request.getStartAt())
                .reservationStartAt(request.getReservationStartAt())
                .price(request.getPrice())
                .totalSeats(request.getTotalSeats())
                .build());
    }

    public List<PerformanceResponse> allPerformance() {
        List<Performance> performances = performanceRepository.findAll();

        return performances.stream()
                .map(performance -> PerformanceResponse.builder()
                        .id(performance.getId())
                        .title(performance.getTitle())
                        .venue(performance.getVenue())
                        .startAt(performance.getStartAt())
                        .reservationStartAt(performance.getReservationStartAt())
                        .price(performance.getPrice())
                        .totalSeats(performance.getTotalSeats())
                        .status(performance.getStatus()).build()).toList();
    }

    public PerformanceResponse getPerformance(Long id) {
        Performance performance = performanceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("찾는 공연이 없습니다."));

        return PerformanceResponse.builder()
                .id(performance.getId())
                .title(performance.getTitle())
                .venue(performance.getVenue())
                .startAt(performance.getStartAt())
                .reservationStartAt(performance.getReservationStartAt())
                .price(performance.getPrice())
                .totalSeats(performance.getTotalSeats())
                .status(performance.getStatus()).build();
    }
}
