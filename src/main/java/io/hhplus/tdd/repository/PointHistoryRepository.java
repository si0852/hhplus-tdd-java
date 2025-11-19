package io.hhplus.tdd.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    public PointHistory insertHistory(PointHistory pointHistory) {
        return pointHistoryTable.insert(pointHistory.userId(), pointHistory.amount(), pointHistory.type(), pointHistory.updateMillis());
    }

    public List<PointHistory> selectHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
