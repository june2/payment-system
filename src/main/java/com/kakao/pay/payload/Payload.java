package com.kakao.pay.payload;

import com.kakao.pay.constant.PaymentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Payload {
    @PayloadField(formatter = PayloadFormatter.STRING, start = 0, length = 10)
    private PaymentType type;

    @PayloadField(formatter = PayloadFormatter.STRING, start = 10, length = 20)
    private String id;

    @PayloadField(formatter = PayloadFormatter.NUMBER_L, start = 30, length = 20)
    private String cardNumber;

    @PayloadField(formatter = PayloadFormatter.NUMBER_0, start = 50, length = 2)
    private Integer paymentMonths;

    @PayloadField(formatter = PayloadFormatter.NUMBER_L, start = 52, length = 4)
    private String cardExpiryDate;

    @PayloadField(formatter = PayloadFormatter.NUMBER_L, start = 56, length = 3)
    private String cardVerificationCode;

    @PayloadField(formatter = PayloadFormatter.NUMBER, start = 59, length = 10)
    private Long paymentPrice;

    @PayloadField(formatter = PayloadFormatter.NUMBER_0, start = 69, length = 10)
    private Long vat;

    @PayloadField(formatter = PayloadFormatter.STRING, start = 79, length = 20)
    private String paymentId;

    @PayloadField(formatter = PayloadFormatter.STRING, start = 99, length = 300)
    private String encryptedCardInfo;

    @PayloadField(formatter = PayloadFormatter.STRING, start = 399, length = 47)
    private String reserved;
}