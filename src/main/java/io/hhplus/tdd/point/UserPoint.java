package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint chargePoint(long amount) {
        long newPoint = this.point + amount;
        return new UserPoint(this.id, newPoint, System.currentTimeMillis());
    }

    public UserPoint usePoint(long amount) {
        long usePoint = this.point - amount;
        if (usePoint < 0) {
            throw new IllegalArgumentException("사용되는 포인트를 확인해주세요");
        }
        return new UserPoint(this.id, usePoint, System.currentTimeMillis());
    }
}
