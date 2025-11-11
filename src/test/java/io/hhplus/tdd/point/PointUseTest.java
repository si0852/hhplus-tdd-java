package io.hhplus.tdd.point;

import io.hhplus.tdd.dto.request.PointUse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PointUseTest {

    PointUse point;
    UserPoint userPoint;


    @Test
    @DisplayName("포인트 사용 요청금액이 0보다 작을떄 에러 발생")
    void pointUse_fail_request_error() {
        // given
        point = PointUse.builder().amount(-1000L).id(1L).build();

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> point.checkAmount());
    }

    @Test
    @DisplayName("포인트 사용시 기존에 가지고 있던 금액에서 사용금액 차감, 차감된 금액이 0보다 작으면 에러 발생")
    void pointUse_minus_point_error() {
        // given
        point = PointUse.builder().amount(10000L).id(1L).build();
        userPoint = new UserPoint(1l, 5000L, System.currentTimeMillis());

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> userPoint.usePoint(point.getAmount()));
    }

    @Test
    @DisplayName("포인트 사용시 기존에 가지고 있던 금액에서 사용금액 차감")
    void pointUse_point() {
        // given
        point = PointUse.builder().amount(10000L).id(1L).build();
        userPoint = new UserPoint(1l, 15000L, System.currentTimeMillis());

        // when
        UserPoint usePoint = userPoint.usePoint(point.getAmount());
        // then
        assertEquals(5000L, usePoint.point());
    }


}
