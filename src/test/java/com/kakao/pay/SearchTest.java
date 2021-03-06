package com.kakao.pay;

import com.kakao.pay.common.CommonTestCase;
import com.kakao.pay.request.payment.ApplyPaymentRequest;
import com.kakao.pay.request.payment.CancelPaymentRequest;
import com.kakao.pay.request.payment.SearchPaymentRequest;
import com.kakao.pay.response.payment.ApplyPaymentResponse;
import com.kakao.pay.response.payment.SearchPaymentResponse;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SearchTest extends CommonTestCase {

    @Test
    public void 조회() throws Throwable {
        ApplyPaymentResponse paymentResponse = (ApplyPaymentResponse) doApply(
                new ApplyPaymentRequest(defaultCardRequest(), 1, 11000L, 1000L),
                status().isOk()
        );

        SearchPaymentRequest searchRequest = SearchPaymentRequest
                .builder()
                .id(paymentResponse.getId())
                .build();

        SearchPaymentResponse searchResponse = (SearchPaymentResponse) doSearch(
                searchRequest,
                status().isOk()
        );

        assertEquals(paymentResponse.getId(), searchResponse.getId());
    }

    @Test
    public void 결제부분취소후_조회() throws Throwable {
        ApplyPaymentResponse paymentResponse = (ApplyPaymentResponse) doApply(
                new ApplyPaymentRequest(defaultCardRequest(), 0, 11000L, 1000L),
                status().isOk()
        );

        SearchPaymentRequest searchRequest = SearchPaymentRequest
                .builder()
                .id(paymentResponse.getId())
                .build();

        assertCancelSuccess(new CancelPaymentRequest(paymentResponse.getId(), 1000L, 100L));

        SearchPaymentResponse searchResponse = (SearchPaymentResponse) doSearch(
                searchRequest,
                status().isOk()
        );

        assertEquals(paymentResponse.getId(), searchResponse.getId());
    }
}