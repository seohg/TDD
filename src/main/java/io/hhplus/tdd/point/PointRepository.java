package io.hhplus.tdd.point;

import java.util.List;

public interface PointRepository{
    UserPoint getUserPointById(Long id);
    UserPoint insertOrUpdateUserPoint(Long id, Long amount);

    PointHistory insertPointHistory(Long id, Long amount, TransactionType transactionType, Long updateMillis);

    List<PointHistory> getAllPointHistoryByUserId(Long id);
}