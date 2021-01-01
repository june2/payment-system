package com.kakao.pay;

import com.kakao.pay.common.CommonTestCase;
import com.kakao.pay.constant.ApiError;
import com.kakao.pay.request.payment.ApplyPaymentRequest;
import com.kakao.pay.request.payment.CancelPaymentRequest;
import com.kakao.pay.request.payment.SearchPaymentRequest;
import com.kakao.pay.response.payment.ApplyPaymentResponse;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Scenario1Test extends CommonTestCase {

    @Test
    public void 부분취소1() throws Throwable {
        ApplyPaymentResponse paymentResponse = (ApplyPaymentResponse) doApply(
                new ApplyPaymentRequest(defaultCardRequest(), 0, 11000L, 1000L),
                status().isOk()
        );

        assertNotNull(paymentResponse);

        assertCancelSuccess(new CancelPaymentRequest(paymentResponse.getId(), 1100L, 100L));
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 9900L, 900L);

        assertCancelSuccess(new CancelPaymentRequest(paymentResponse.getId(), 3300L, null));
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 6600L, 600L);

        assertCancelFailed(
                new CancelPaymentRequest(paymentResponse.getId(), 7000L, null),
                ApiError.NOT_ENOUGH_PRICE
        );
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 6600L, 600L);

        assertCancelFailed(
                new CancelPaymentRequest(paymentResponse.getId(), 6600L, 700L),
                ApiError.NOT_ENOUGH_VAT
        );
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 6600L, 600L);

        assertCancelSuccess(new CancelPaymentRequest(paymentResponse.getId(), 6600L, 600L));
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 0L, 0L);

        assertCancelFailed(
                new CancelPaymentRequest(paymentResponse.getId(), 100L, null),
                ApiError.NOT_ENOUGH_PRICE
        );
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 0L, 0L);
    }
}