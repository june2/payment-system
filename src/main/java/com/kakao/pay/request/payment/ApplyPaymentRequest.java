package com.kakao.pay.request.payment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kakao.pay.request.CardRequest;
import com.kakao.pay.request.PriceRequest;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class ApplyPaymentRequest extends PriceRequest {
    @NotNull(message = "{validation.constraints.cardRequest.notNull}")
    @Valid
    private CardRequest card;

    @NotNull(message = "{validation.constraints.paymentMonth.notNull}")
    @Min(value = 0, message = "{validation.constraints.paymentMonth.min}")
    @Max(value = 12, message = "{validation.constraints.paymentMonth.max}")
    private Integer month;

    @JsonCreator
    private ApplyPaymentRequest() {
        super();
    }

    @Builder
    public ApplyPaymentRequest(CardRequest card, Integer month, Long price, Long vat) {
        super(price, vat);
        this.card = card;
        this.month = month;
    }

    public String getMonth() {
        if (month < 10) {
            return "0" + month;
        } else {
            return month.toString();
        }
    }
}