package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointCheckTest {

    @Mock
    private UserPointTable userPoint;

    @InjectMocks
    private PointService pointService;

    @Test
    void 포인트조회_유저존재여부_O() {
        // given
        long id = 1l;
        UserPoint user = new UserPoint(id, 0, 10L);
        when(userPoint.selectById(id)).thenReturn(user);

        // when
        UserPoint user2 = pointService.checkPoint(id);

        // then
        assertEquals(user, user2);
    }

    @Test
    void 포인트조회_유저존재여부_X() {
        // given
        long id = 1l;
        UserPoint user = null;
        when(userPoint.selectById(id)).thenReturn(user);

        // when
        UserPoint user2 = pointService.checkPoint(id);

        // then
        assertThat(user2).isNull();
    }

    @Test
    void 포인트조회_유저존재여부_X_에러() {
        // given
        long id = 1l;
        UserPoint user = null;
        when(userPoint.selectById(id)).thenReturn(user);

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> pointService.checkPoint(id));
    }

    @Test
    void 포인트조회_포인트_0원() {
        // given
        long id = 1l;
        UserPoint user = new UserPoint(id, 0, 10L);
        when(userPoint.selectById(id)).thenReturn(user);

        // when
        UserPoint user2 = pointService.checkPoint(id);

        // then
        assertEquals(user2.point(), 0);
    }

    @Test
    void 포인트조회_포인트_0원이_아닐때() {
        // given
        long id = 1l;
        UserPoint user = new UserPoint(id, 1000, 10L);
        when(userPoint.selectById(id)).thenReturn(user);

        // when
        UserPoint user2 = pointService.checkPoint(id);

        // then
        assertEquals(user2.point(), 1000);
    }
}
