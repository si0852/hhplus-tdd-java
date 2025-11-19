package io.hhplus.tdd.point;

import io.hhplus.tdd.dto.request.PointCharge;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PointChargeTest {

    PointCharge point;
    UserPoint userPoint;


    @Test
    void 포인트충전_요청금액_0보다_작을때_에러발생() {
        // given
        point = PointCharge.builder().amount(-1000).id(1L).build();

        // when
        // then
        assertThrows(IllegalArgumentException.class, () -> point.checkAmount());
    }

    @Test
    void 포인트충전_충전금액은_가산된다() {
        // given
        point = PointCharge.builder().amount(1000).id(1L).build();
        userPoint = new UserPoint(1l, 5000, System.currentTimeMillis());

        // when
        UserPoint newUserPoint = userPoint.chargePoint(point.getAmount());

        // then
        assertEquals(newUserPoint.point(), 6000L);
    }

    @Test
    @DisplayName("충전금액이 1_000_000이 넘을 경우")
    void 포인트충전_충전금액이_MAX_값보다_클떄() {
        // given
        userPoint = new UserPoint(1l, 5000, System.currentTimeMillis());

        // when
        // then
        assertThrows(IllegalArgumentException.class, () ->
                userPoint.chargePoint(996000));
    }
}
