package com.kakao.pay;

import com.kakao.pay.constant.ApiError;
import com.kakao.pay.request.payment.ApplyPaymentRequest;
import com.kakao.pay.request.payment.CancelPaymentRequest;
import com.kakao.pay.request.payment.SearchPaymentRequest;
import com.kakao.pay.response.ErrorResponse;
import com.kakao.pay.response.payment.ApplyPaymentResponse;
import com.kakao.pay.response.payment.SearchPaymentResponse;
import com.kakao.pay.common.CommonTestCase;
import junit.framework.TestCase;
import org.junit.Test;

import static junit.framework.TestCase.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PaymentTest extends CommonTestCase {
    @Test
    public void 결제_data_size_is_450() throws Throwable {
        ApplyPaymentResponse paymentResponse = (ApplyPaymentResponse) doApply(
                new ApplyPaymentRequest(defaultCardRequest(), 0, 11000L, 1000L),
                status().isOk()
        );

        assertNotNull(paymentResponse);
        assertEquals(paymentResponse.getData().length(), 450);
    }

    @Test
    public void 전체취소() throws Throwable {
        ApplyPaymentResponse paymentResponse = (ApplyPaymentResponse) doApply(
                new ApplyPaymentRequest(defaultCardRequest(), 0, 11000L, 1000L),
                status().isOk()
        );

        assertNotNull(paymentResponse);
        assertCancelSuccess(new CancelPaymentRequest(paymentResponse.getId(), 11000L, 1000L));
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 0L, 0L);
    }

    @Test
    public void 결제에_대한_전체취소는_1번만_가능() throws Throwable {
        ApplyPaymentResponse paymentResponse = (ApplyPaymentResponse) doApply(
                new ApplyPaymentRequest(defaultCardRequest(), 0, 11000L, 1000L),
                status().isOk()
        );

        assertNotNull(paymentResponse);

        assertCancelSuccess(new CancelPaymentRequest(paymentResponse.getId(), 11000L, 1000L));
        assertSearchSuccess(new SearchPaymentRequest(paymentResponse.getId()), 0L, 0L);

        assertCancelFailed(
                new CancelPaymentRequest(paymentResponse.getId(), 1000L, 100L),
                ApiError.NOT_ENOUGH_PRICE
        );
    }
}