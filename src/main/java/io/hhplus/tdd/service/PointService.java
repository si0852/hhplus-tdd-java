package io.hhplus.tdd.service;


import io.hhplus.tdd.dto.request.PointCharge;
import io.hhplus.tdd.dto.request.PointUse;
import io.hhplus.tdd.dto.response.PointHistoryDto;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public UserPoint checkPoint(long id) {
        return userCheckValue(id);
    }

    public UserPoint chargePoint(PointCharge point) {
        point.checkAmount();

        UserPoint user = userCheckValue(point.getId());
        UserPoint newUserPoint = user.chargePoint(point.getAmount());

        UserPoint successPoint = userPointRepository.savePoint(newUserPoint);
        pointHistoryRepository.insertHistory(new PointHistory(0, successPoint.id(), point.getAmount(), TransactionType.CHARGE, System.currentTimeMillis()));

        return successPoint;
    }

    public UserPoint usePoint(PointUse point) {
        point.checkAmount();

        UserPoint user = userCheckValue(point.getId());
        UserPoint useUserPoint = user.usePoint(point.getAmount());

        UserPoint userPoint = userPointRepository.updatePoint(useUserPoint);
        pointHistoryRepository.insertHistory(new PointHistory(0, userPoint.id(), point.getAmount(), TransactionType.USE, System.currentTimeMillis()));

        return userPoint;
    }

    public List<PointHistoryDto> selectHistory(long userId) {
        UserPoint userPoint = userCheckValue(userId);
        List<PointHistory> histories = pointHistoryRepository.selectHistory(userPoint.id());

        List<PointHistoryDto> userHistories = histories.stream()
                .map(history -> PointHistoryDto.builder()
                        .userId(history.userId())
                        .type(history.type())
                        .amount(history.amount())
                        .build()).toList();

        return userHistories;
    }

    private UserPoint userCheckValue(long useId) {
        UserPoint user = userPointRepository.selectUser(useId);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("존재하지 않는 유저 정보 입니다.");
        }
        return user;
    }
}
