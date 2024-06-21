package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PointRepositoryImpl implements PointRepository {

    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;

    public PointRepositoryImpl(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    @Override
    public UserPoint getUserPointById(Long id) {
        return userPointTable.selectById(id);
    }
    @Override
    public UserPoint insertOrUpdateUserPoint(Long id, Long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }

    @Override
    public PointHistory insertPointHistory(Long id, Long amount, TransactionType transactionType, Long updateMillis) {
        return pointHistoryTable.insert(id, amount, transactionType, updateMillis);
    }

    @Override
    public List<PointHistory> getAllPointHistoryByUserId(Long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }


}