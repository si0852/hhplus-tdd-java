package io.hhplus.tdd.service;

import io.hhplus.tdd.dto.PointCharge;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private UserPointRepository userPointRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("유저가 존재하지 않으면 예외 발생")
    void getPoint_userNotFound() {
        //given
        when(userPointRepository.selectUser(999L)).thenReturn(null);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> pointService.userCheckValue(999L));
    }

    @Test
    @DisplayName("포인트 충전시 포인트가 0보다 작은 요청이 들어오면 예외 발생")
    void getPoint_fail_request_throw_error() {
        //given
        PointCharge charge = PointCharge.builder().id(1L).amount(-100L).build();

        // when & then
        assertThrows(IllegalArgumentException.class, () -> pointService.chargePoint(charge));
    }

    @Test
    @DisplayName("포인트 충전 성공")
    void getPoint_Success_Point() {
        //given
        PointCharge request = PointCharge.builder().id(1L).amount(1000L).build();

        UserPoint existsUser = new UserPoint(request.getId(), 500L, System.currentTimeMillis());
        UserPoint chargedUser = new UserPoint(request.getId(), 1500L, System.currentTimeMillis());

        when(userPointRepository.selectUser(1L)).thenReturn(existsUser);
        when(userPointRepository.savePoint(any(UserPoint.class))).thenReturn(chargedUser);

        //when
        UserPoint chargingPoint = pointService.chargePoint(request);

        //then
        assertEquals(1500L, chargingPoint.point());

    }

}
