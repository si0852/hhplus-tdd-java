package io.hhplus.tdd.dto.response;

import io.hhplus.tdd.point.TransactionType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointHistoryDto {
    long userId;
    long amount;
    TransactionType type;
}
