package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable pointTable;

    public UserPoint checkPoint(long id) {
        UserPoint user = pointTable.selectById(id);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("존재하지 않는 유저 정보 입니다.");
        }
        return user;
    }
}
