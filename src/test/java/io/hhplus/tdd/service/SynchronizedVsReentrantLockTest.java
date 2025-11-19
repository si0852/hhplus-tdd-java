package io.hhplus.tdd.service;

import io.hhplus.tdd.dto.request.PointCharge;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.repository.PointHistoryRepository;
import io.hhplus.tdd.repository.UserPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SynchronizedVsReentrantLockTest {

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Service
    static class PointServiceWithSynchronized {

        private final UserPointRepository userPointRepository;
        private final PointHistoryRepository pointHistoryRepository;

        private final ConcurrentHashMap<Long, Object> userLocks = new ConcurrentHashMap<>();

        public PointServiceWithSynchronized(UserPointRepository userPointRepository, PointHistoryRepository pointHistoryRepository) {
            this.userPointRepository = userPointRepository;
            this.pointHistoryRepository = pointHistoryRepository;
        }

        public UserPoint chargePoint(PointCharge point) {
            point.checkAmount();
            Object lock = userLocks.computeIfAbsent(point.getId(), id -> new Object());

            synchronized (lock) {
                UserPoint user = userPointRepository.selectUser(point.getId());
                if (user == null) {
                    throw new IllegalArgumentException("존재하지 않는 유저 입니다.");
                }
                UserPoint newUserPoint = user.chargePoint(point.getAmount());
                UserPoint savedPoint = userPointRepository.savePoint(newUserPoint);

                pointHistoryRepository.insertHistory(
                        new PointHistory(0, savedPoint.id(), point.getAmount(),
                                TransactionType.CHARGE, System.currentTimeMillis())
                );

                return savedPoint;
            }

        }
    }

    @Service
    static class PointServiceWithReentrantLock {
        private final UserPointRepository userPointRepository;
        private final PointHistoryRepository pointHistoryRepository;

        private final ConcurrentHashMap<Long, Lock> userLocks = new ConcurrentHashMap<>();

        public PointServiceWithReentrantLock(UserPointRepository userPointRepository, PointHistoryRepository pointHistoryRepository) {
            this.userPointRepository = userPointRepository;
            this.pointHistoryRepository = pointHistoryRepository;
        }


        public UserPoint chargePoint(PointCharge point) {
            point.checkAmount();

            Lock lock = userLocks.computeIfAbsent(point.getId(), id -> new ReentrantLock(false));

            lock.lock();

            try {
                UserPoint user = userPointRepository.selectUser(point.getId());
                if (user == null) {
                    throw new IllegalArgumentException("존재하지 않는 유저");
                }

                UserPoint newUserPoint = user.chargePoint(point.getAmount());
                UserPoint savedPoint = userPointRepository.savePoint(newUserPoint);

                pointHistoryRepository.insertHistory(
                        new PointHistory(0, savedPoint.id(), point.getAmount(),
                                TransactionType.CHARGE, System.currentTimeMillis())
                );

                return savedPoint;
            } finally {
                lock.unlock();
            }

        }
    }

    @Service
    static class PointServiceWithFairReentrantLock {
        private final UserPointRepository userPointRepository;
        private final PointHistoryRepository pointHistoryRepository;

        private final ConcurrentHashMap<Long, Lock> userLocks = new ConcurrentHashMap<>();

        public PointServiceWithFairReentrantLock(UserPointRepository userPointRepository, PointHistoryRepository pointHistoryRepository) {
            this.userPointRepository = userPointRepository;
            this.pointHistoryRepository = pointHistoryRepository;
        }


        public UserPoint chargePoint(PointCharge point) {
            point.checkAmount();

            Lock lock = userLocks.computeIfAbsent(point.getId(), id -> new ReentrantLock(true));

            lock.lock();

            try {
                UserPoint user = userPointRepository.selectUser(point.getId());
                if (user == null) {
                    throw new IllegalArgumentException("존재하지 않는 유저");
                }

                UserPoint newUserPoint = user.chargePoint(point.getAmount());
                UserPoint savedPoint = userPointRepository.savePoint(newUserPoint);

                pointHistoryRepository.insertHistory(
                        new PointHistory(0, savedPoint.id(), point.getAmount(),
                                TransactionType.CHARGE, System.currentTimeMillis())
                );

                return savedPoint;
            } finally {
                lock.unlock();
            }

        }
    }

      private PointServiceWithSynchronized syncService;                                                                                                                                  
      private PointServiceWithReentrantLock reentrantService;
      private PointServiceWithFairReentrantLock fairReentrantService;
                                                                                                                                                                                                     
      @BeforeEach
      void setUp() {
          syncService = new PointServiceWithSynchronized(userPointRepository, pointHistoryRepository);
          reentrantService = new PointServiceWithReentrantLock(userPointRepository, pointHistoryRepository);
          fairReentrantService = new PointServiceWithFairReentrantLock(userPointRepository, pointHistoryRepository);
      }

    @Test
    @DisplayName("성능 비교: synchronized vs ReentrantLock(unfair) vs ReentrantLock(fair)")
    void 성능비교_제어방식() throws InterruptedException  {

        int threadCount = 100;
        long chargeAmount = 100L;

        long syncTime = measurePerformance("synchronized", syncService, 1L, threadCount, chargeAmount);

        long reentrantTime = measurePerformance("ReentrantLock(Unfair)", reentrantService, 2L, threadCount, chargeAmount);

        long fairTime = measurePerformance("ReentrantLock(Fair)", fairReentrantService, 3L, threadCount, chargeAmount);

        System.out.printf("synchronized        : %d ms (TPS: %.2f)\n", syncTime, (threadCount * 1000.0) / syncTime);
        System.out.printf("ReentrantLock(Unfair): %d ms (TPS: %.2f)\n", reentrantTime, (threadCount * 1000.0) / reentrantTime);
        System.out.printf("ReentrantLock(Fair)  : %d ms (TPS: %.2f)\n", fairTime, (threadCount * 1000.0) / fairTime);

    }

    private long measurePerformance(String name, Object service, long userId, int threadCount, long chargeAmount) throws InterruptedException {
        // 초기 유저 생성
        userPointRepository.savePoint(new UserPoint(userId, 0L, System.currentTimeMillis()));

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        long startTime = System.currentTimeMillis();

        // 동시 요청 실행
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    PointCharge charge = PointCharge.builder()
                            .id(userId)
                            .amount(chargeAmount)
                            .build();

                    if (service instanceof PointServiceWithSynchronized) {
                        ((PointServiceWithSynchronized) service).chargePoint(charge);
                    } else if (service instanceof PointServiceWithReentrantLock) {
                        ((PointServiceWithReentrantLock) service).chargePoint(charge);
                    } else if (service instanceof PointServiceWithFairReentrantLock) {
                        ((PointServiceWithFairReentrantLock) service).chargePoint(charge);
                    }

                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        long endTime = System.currentTimeMillis();
        executorService.shutdown();

        long duration = endTime - startTime;

        // 검증
        UserPoint finalUser = userPointRepository.selectUser(userId);
        long expectedPoint = chargeAmount * threadCount;

        System.out.printf("[%s] 성공: %d, 실패: %d, 소요시간: %dms, 최종포인트: %d (예상: %d)\n",
                name, successCount.get(), failCount.get(), duration, finalUser.point(), expectedPoint);

        assertEquals(expectedPoint, finalUser.point(), name + " 정합성 검증 실패");

        return duration;
    }
}