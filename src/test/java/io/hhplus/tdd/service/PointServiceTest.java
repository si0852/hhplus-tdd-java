package io.hhplus.tdd.service;

import io.hhplus.tdd.dto.PointCharge;
import io.hhplus.tdd.dto.PointUse;
import io.hhplus.tdd.point.PointHistory;
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
        PointCharge charge = PointCharge.builder().id(999L).amount(100L).build();
        when(userPointRepository.selectUser(charge.getId())).thenReturn(null);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> pointService.chargePoint(charge));
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

    @Test
    @DisplayName("포인트 사용시 기존 포인트보다 많은 포인트를 사용하면 에러발생")
    void usePoint_minus_point_error() {
        //given
        PointUse request = PointUse.builder().id(1L).amount(1000L).build();

        UserPoint existsUser = new UserPoint(request.getId(), 500L, System.currentTimeMillis());

        when(userPointRepository.selectUser(1L)).thenReturn(existsUser);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> pointService.usePoint(request));
    }

    @Test
    @DisplayName("포인트 사용 성공")
    void usePoint_success() {
        //given
        PointUse request = PointUse.builder().id(1L).amount(1000L).build();

        UserPoint existsUser = new UserPoint(request.getId(), 5000L, System.currentTimeMillis());
        UserPoint usedUser = new UserPoint(request.getId(), 4000L, System.currentTimeMillis());

        when(userPointRepository.selectUser(1L)).thenReturn(existsUser);
        when(userPointRepository.updatePoint(any(UserPoint.class))).thenReturn(usedUser);

        // when
        UserPoint chargingPoint = pointService.usePoint(request);

        // then
        assertEquals(4000L, chargingPoint.point());
        verify(pointHistoryRepository).insertHistory(any(PointHistory.class));
    }

}
