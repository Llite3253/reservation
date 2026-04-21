package com.llite.reservation.Enum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PerformanceStatus {
    READY("오픈 대기"),
    OPEN("예매 중"),
    CLOSED("예매 마감"),
    CANCEL("공연 취소");

    private final String description;
}
