package com.kakao.pay;

import com.kakao.pay.response.ErrorResponse;
import com.kakao.pay.common.CommonTestCase;
import com.kakao.pay.constant.ApiError;
import com.kakao.pay.request.payment.ApplyPaymentRequest;
import com.kakao.pay.request.payment.CancelPaymentRequest;
import com.kakao.pay.response.payment.ApplyPaymentResponse;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MultiThreadTest extends CommonTestCase {
    @Test
    public void 하나의_카드번호로_동시에_결제() {
        ApplyPaymentRequest applyPaymentRequest = ApplyPaymentRequest
                .builder()
                .card(defaultCardRequest())
                .month(10)
                .price(1000L)
                .build();

        List<ErrorResponse> errors = new ArrayList<>();

        Runnable worker = () -> {
            try {
                Object result = doApply(applyPaymentRequest, null);

                if (result instanceof ErrorResponse) {
                    ErrorResponse errorResponse = (ErrorResponse) result;

                    if (errorResponse.getApiError() == ApiError.CARD_LOCKED) {
                        errors.add(errorResponse);
                    }
                }
            } catch (Throwable ignored) {
            }
        };

        CompletableFuture
                .allOf(CompletableFuture.runAsync(worker), CompletableFuture.runAsync(worker), CompletableFuture.runAsync(worker))
                .join();

        Assert.assertEquals(errors.size(), 2);
    }

    @Test
    public void  동일거래로_동시에_전체_부분_취소() throws Throwable {
        ApplyPaymentResponse paymentResponse = (ApplyPaymentResponse) doApply(
                new ApplyPaymentRequest(defaultCardRequest(), 0, 11000L, 1000L),
                status().isOk()
        );

        if (paymentResponse != null) {
            CancelPaymentRequest cancelPaymentRequest = CancelPaymentRequest
                    .builder()
                    .paymentId(paymentResponse.getId())
                    .price(1000L)
                    .build();

            List<ErrorResponse> errors = new ArrayList<>();

            Runnable worker = () -> {
                try {
                    Object result = doCancel(cancelPaymentRequest, null);

                    if (result instanceof ErrorResponse) {
                        ErrorResponse errorResponse = (ErrorResponse) result;

                        if (errorResponse.getApiError() == ApiError.PAYMENT_LOCKED) {
                            errors.add(errorResponse);
                        }
                    }
                } catch (Throwable ignored) {
                }
            };

            CompletableFuture
                    .allOf(CompletableFuture.runAsync(worker), CompletableFuture.runAsync(worker), CompletableFuture.runAsync(worker))
                    .join();

            Assert.assertEquals(errors.size(), 2);
        }
    }
}