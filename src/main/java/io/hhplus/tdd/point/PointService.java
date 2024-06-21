package io.hhplus.tdd.point;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PointService {
    private final PointRepository pointRepository;

    public PointService(PointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    public UserPoint getPoint(Long id) {
        return pointRepository.getUserPointById(id);
    }

    public List<PointHistory> getHistoryList(Long id) {
        return pointRepository.getAllPointHistoryByUserId(id);
    }

    public synchronized UserPoint chargePoint(Long id, Long amount) {

        // 포인트 체크
        validatePoint(amount);

        // 포인트 조회
        UserPoint userPoint = pointRepository.getUserPointById(id);

        // 포인트 충전
        UserPoint result = pointRepository.insertOrUpdateUserPoint(id, userPoint.point() + amount);

        // 포인트 충전 내역 저장
        pointRepository.insertPointHistory(id, amount, TransactionType.CHARGE, result.updateMillis());

        return result;
    }

    public synchronized UserPoint usePoint(Long id, Long amount) {

        // 포인트 체크
        validatePoint(amount);

        // 포인트 조회
        UserPoint userPoint = pointRepository.getUserPointById(id);

        if (amount > userPoint.point()) {
            throw new IllegalArgumentException("[잔고부족]으로 인한 포인트 사용 실패");
        }

        // 포인트 사용
        UserPoint result = pointRepository.insertOrUpdateUserPoint(id, userPoint.point() - amount);

        // 포인트 사용 내역 저장
        pointRepository.insertPointHistory(id, amount, TransactionType.USE, result.updateMillis());

        return result;
    }
    public void validatePoint(Long amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("입력 포인트가 0보다 작습니다.");
        }
    }




}
