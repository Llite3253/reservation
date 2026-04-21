package com.llite.reservation.Controller;

import com.llite.reservation.Dto.Reqeust.PerformanceRequest;
import com.llite.reservation.Dto.Response.PerformanceResponse;
import com.llite.reservation.Service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performance")
public class PerformanceController {
    private final PerformanceService performanceService;

    @GetMapping("/all")
    public ResponseEntity<List<PerformanceResponse>> allPerformance() {
        List<PerformanceResponse> performances = performanceService.allPerformance();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(performances);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceResponse> getPerformance(@PathVariable Long id) {
        PerformanceResponse performance = performanceService.getPerformance(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(performance);
    }

    @PostMapping
    public ResponseEntity<Void> createPerformance(@RequestBody PerformanceRequest request) {
        performanceService.createPerformance(request);
        return ResponseEntity.ok().build();
    }
}
