package com.kakao.pay.constant;

import lombok.Getter;

public enum ApiError {
    ERROR(00, "오류"),
    INVALID_PARAMETER(01, "누락 또는 잘못된 형식으로 입력했습니다."),
    CARD_LOCKED(02, "현재 결제가 진행 중인 카드 정보는 사용할 수 없습니다."),
    PAYMENT_LOCKED(03, "현재 취소가 진행 중인 결제 ID는 사용할 수 없습니다."),
    PAYMENT_NOT_FOUND(04, "결제 정보를 찾을 수 없습니다."),
    NOT_ENOUGH_PRICE(05, "금액이 부족합니다."),
    NOT_ENOUGH_VAT(06, "VAT가 부족합니다."),
    VAT_GREATER_THAN_PRICE(07, "VAT가 금액보다 더 큽니다.");

    @Getter
    private final int code;

    @Getter
    private final String description;

    ApiError(int code, String description) {
        this.code = code;
        this.description = description;
    }
}