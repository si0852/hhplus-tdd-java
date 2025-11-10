package io.hhplus.tdd.repository;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPointRepository {

    private final UserPointTable userPointTable;

    public UserPoint selectUser(long id) {
        return userPointTable.selectById(id);}

    public UserPoint savePoint(UserPoint userPoint) {
       return userPointTable.insertOrUpdate(userPoint.id(), userPoint.point());
    }
}
