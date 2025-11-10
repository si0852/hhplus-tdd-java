package io.hhplus.tdd.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointUse {

    private long id;
    private long amount;

    public void checkAmount() {
        if(this.amount < 0){
            throw new IllegalArgumentException("포인트를 확인해주세요");
        }
    }

}
