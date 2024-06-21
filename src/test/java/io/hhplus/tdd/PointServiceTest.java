package io.hhplus.tdd;

import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {
    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    PointService pointService;
    private static final long ID = 1L;
    private static final long AMOUNT = 500L;

    @Test
    @DisplayName("ID로 사용자 포인트 조회")
    void getUserPointById() {
        // Given
        long id = 1L;
        long amount = 500L;
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());

        given(pointRepository.getUserPointById(anyLong())).willReturn(userPoint);

        // When
        UserPoint getUserPoint = pointService.getPoint(1L);

        // Then
        verify(pointRepository, times(1)).getUserPointById(1L);
        assertThat(getUserPoint.point()).isEqualTo(500L);
    }


    @Test
    @DisplayName("ID로 사용자 포인트 기록 조회")
    void getUserPointHistoryTest() {
        // Given
        List<PointHistory> pointHistoryList = new ArrayList<>();
        pointHistoryList.add(new PointHistory(1, ID, AMOUNT, TransactionType.CHARGE, System.currentTimeMillis()));
        pointHistoryList.add(new PointHistory(2, ID, AMOUNT, TransactionType.USE, System.currentTimeMillis()));

        given(pointRepository.getAllPointHistoryByUserId(anyLong())).willReturn(pointHistoryList);

        // When
        List<PointHistory> result = pointService.getHistoryList(ID);

        // Then
        verify(pointRepository, times(1)).getAllPointHistoryByUserId(ID);
        assertThat(result).isEqualTo(pointHistoryList);
    }


    @Test
    @DisplayName("포인트 충전")
    void chargePointTest() {
        UserPoint userPoint = new UserPoint(ID, AMOUNT, System.currentTimeMillis());
        UserPoint chargePoint = new UserPoint(ID, AMOUNT, System.currentTimeMillis());

        given(pointRepository.getUserPointById(anyLong())).willReturn(userPoint);
        given(pointRepository.insertOrUpdateUserPoint(anyLong(), anyLong())).willReturn(chargePoint);

        // WHEN
        UserPoint result = pointService.chargePoint(ID, AMOUNT);

        // THEN
        verify(pointRepository, times(1)).insertOrUpdateUserPoint(ID, AMOUNT * 2);
        assertThat(result).isEqualTo(userPoint);
    }

    @Test
    @DisplayName("포인트 사용")
    void usePointTest() {
        //Given
        Long useAmount = 100L;
        UserPoint userPoint = new UserPoint(ID, AMOUNT, System.currentTimeMillis());
        UserPoint usePoint = new UserPoint(ID, AMOUNT - 100L, System.currentTimeMillis());

        given(pointRepository.getUserPointById(anyLong())).willReturn(userPoint);
        given(pointRepository.insertOrUpdateUserPoint(anyLong(), anyLong())).willReturn(usePoint);

        // When
        UserPoint result = pointService.usePoint(ID, useAmount);

        // Then
        verify(pointRepository, times(1)).insertOrUpdateUserPoint(ID, AMOUNT - 100L);
        assertThat(result).isEqualTo(usePoint);
    }


    @Test
    @DisplayName("0보다 작은 입력값이 들어온 경우 충전/사용 불가")
    void validationPointFailTest() {
        Long amount = -400L;
        assertThrows(IllegalArgumentException.class, () -> pointService.chargePoint(ID, amount));
        assertThrows(IllegalArgumentException.class, () -> pointService.usePoint(ID, amount));
    }

    @Test
    @DisplayName("잔액부족시 포인트 사용 실패")
    void failToUsePointTest() {
        UserPoint currentUserPoint = new UserPoint(ID, 100, System.currentTimeMillis());
        when(pointRepository.getUserPointById(anyLong())).thenReturn(currentUserPoint);

        assertThrows(IllegalArgumentException.class, () -> pointService.usePoint(ID, 600L));
    }

}
