package com.kakao.pay.response.payment;

import com.kakao.pay.constant.PaymentType;
import com.kakao.pay.model.card.MaskedCardInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchPaymentResponse {
    private String id;

    private MaskedCardInfo cardInfo;

    private PaymentType paymentType;

    private Long price;

    private Long vat;

    private String month;

    private Object optional;
}