package io.hhplus.tdd.point;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    private static final long MAX_POINT = 1_000_000L;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint chargePoint(long amount) {
        long newPoint = this.point + amount;
        validatePoint(newPoint);
        return new UserPoint(this.id, newPoint, System.currentTimeMillis());
    }

    public UserPoint usePoint(long amount) {
        long usePoint = this.point - amount;
        if (usePoint < 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        return new UserPoint(this.id, usePoint, System.currentTimeMillis());
    }

    private void validatePoint(long point) {
        if (point > MAX_POINT) {
            throw new IllegalArgumentException("포인트는 최대 " + MAX_POINT + "까지 가능합니다.");
        }
    }
}
