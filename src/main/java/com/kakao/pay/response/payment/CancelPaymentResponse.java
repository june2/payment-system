package com.kakao.pay.response.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;

@Data
public class CancelPaymentResponse {
    private String id;

    @Builder
    public CancelPaymentResponse(String id) {
        this.id = id;
    }

    @JsonCreator
    private CancelPaymentResponse() {
        super();
    }
}