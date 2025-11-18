package io.hhplus.tdd.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointUse {

    private long id;
    private long amount;

    public void checkAmount() {
        if(this.amount < 0){
            throw new IllegalArgumentException("사용 금액은 0보다 커야한다.");
        }
    }

}
