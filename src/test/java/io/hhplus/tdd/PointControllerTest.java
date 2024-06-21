package io.hhplus.tdd;

import io.hhplus.tdd.point.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PointController.class)
@AutoConfigureMockMvc
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PointService pointService;

    @Test
    @DisplayName("[GET] 유저의 포인트 조회")
    void getPointTest() throws Exception {

        // Given
        Long id = 1L;
        Long amount = 500L;
        UserPoint userPoint = new UserPoint(id, amount, System.currentTimeMillis());

        given(pointService.getPoint(id)).willReturn(userPoint);

        // When & Then
        mockMvc.perform(get("/point/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.point").value(500))
                .andDo(print()); // 반환값 확인

        verify(pointService).getPoint(id); // pointService 메소드 호출 확인
    }

    @Test
    @DisplayName("[GET] 포인트 내역 조회")
    public void getPointHistoryTest() throws Exception {
        // Given
        Long id = 1L;
        Long amount = 500L;
        List<PointHistory> historyList = new ArrayList<>();
        historyList.add(new PointHistory(1, id, amount, TransactionType.CHARGE, System.currentTimeMillis()));

        given(pointService.getHistoryList(id)).willReturn(historyList);

        // When & Then
        mockMvc.perform(get("/point/{id}/histories", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1)) // 첫 번째 항목의 값을 비교
                .andExpect(jsonPath("$[0].userId").value(id))
                .andExpect(jsonPath("$[0].amount").value(amount))
                .andDo(print()); // 반환값 확인

        verify(pointService).getHistoryList(id); // 메소드 호출 확인
    }

    @Test
    @DisplayName("[PATCH] 포인트 충전")
    public void chargePointTest() throws Exception {
        // Given
        Long id = 1L;
        Long amount = 500L;
        UserPoint updatedPoint = new UserPoint(id, amount, 0L);
        given(pointService.chargePoint(id, amount)).willReturn(updatedPoint);

        // When & Then
        mockMvc.perform(patch("/point/{id}/charge", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.point").value(amount))
                .andDo(print()); // 반환값 확인

        verify(pointService).chargePoint(id, amount); // 메소드 호출 확인

    }

    @Test
    @DisplayName("[PATCH] 포인트 사용")
    public void usePointTest() throws Exception {
        // Given
        Long id = 1L;
        Long amount = 500L;
        UserPoint updatedPoint = new UserPoint(id, amount, 0L);
        given(pointService.usePoint(id, amount)).willReturn(updatedPoint);

        // when
        mockMvc.perform(patch("/point/{id}/use", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(String.valueOf(amount)))
                .andExpect(status().isOk())
                .andDo(print()); // 반환값 확인

        verify(pointService).usePoint(id, amount); // 메소드 호출 확인
    }


}
