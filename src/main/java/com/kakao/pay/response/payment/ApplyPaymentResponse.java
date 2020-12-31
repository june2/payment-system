package com.kakao.pay.response.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApplyPaymentResponse {
    private String id;
    private String data;

    @JsonCreator
    private ApplyPaymentResponse() {
        super();
    }
}