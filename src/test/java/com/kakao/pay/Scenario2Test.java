package com.kakao.pay;

import com.kakao.pay.common.CommonTestCase;
import com.kakao.pay.constant.ApiError;
import com.kakao.pay.request.payment.ApplyPaymentRequest;
import com.kakao.pay.request.payment.CancelPaymentRequest;
import com.kakao.pay.request.payment.SearchPaymentRequest;
import com.kakao.pay.response.payment.ApplyPaymentResponse;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Scenario2Test extends CommonTestCase {
    @Test
    public void 부분취소2() throws Throwable {
        ApplyPaymentResponse paymentResponse = (ApplyPaymentResponse) doApply(
                new ApplyPaymentRequest(defaultCardRequest(), 0, 20000L, 909L),
                status().isOk()
        );

        assertNotNull(paymentResponse);
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 20000L, 909L);

        assertCancelSuccess(new CancelPaymentRequest(paymentResponse.getId(), 10000L, 0L));
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 10000L, 909L);

        assertCancelFailed(
                new CancelPaymentRequest(paymentResponse.getId(), 10000L, 0L),
                ApiError.VAT_GREATER_THAN_PRICE
        );
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 10000L, 909L);

        assertCancelSuccess(new CancelPaymentRequest(paymentResponse.getId(), 10000L, 909L));
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 0L, 0L);
    }
}