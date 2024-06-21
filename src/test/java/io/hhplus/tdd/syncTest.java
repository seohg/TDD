package io.hhplus.tdd;

import io.hhplus.tdd.point.PointRepository;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class syncTest {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private PointService pointService;

    private static final long ID = 1L;
    private static final long AMOUNT = 500L;

    @Test
    @DisplayName("동시성 테스트 : 포인트 정상 차감 확인")
    void syncTest() throws InterruptedException {

        // 멀티 스레드 이용
        int numberOfThreads = 2;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        pointService.chargePoint(ID, AMOUNT);

        service.execute(() -> {
            pointService.usePoint(ID, 100L);
            latch.countDown();
        });
        service.execute(() -> {
            pointService.usePoint(ID, 200L);
            latch.countDown();
        });

        latch.await(); // latch count가 0이 될 때까지 기다림

        //Then
        UserPoint point =  pointService.getPoint(ID);
        assertThat(point.point()).isEqualTo(AMOUNT-100-200);
    }

}
