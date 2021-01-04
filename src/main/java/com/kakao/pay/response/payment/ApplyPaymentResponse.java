package com.kakao.pay.response.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApplyPaymentResponse {
    private String id;
    private String data;
}