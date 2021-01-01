package com.kakao.pay.request.payment;

import com.kakao.pay.common.CommonTestCase;
import org.junit.Test;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SearchPaymentRequestTest extends CommonTestCase {
    @Test
    public void ID_누락() {
        SearchPaymentRequest request = defaultSearchRequest();
        request.setId(null);
        assertConstraint(request, NotNull.class);
    }

    @Test
    public void ID_잘못된_길이() {
        SearchPaymentRequest request = defaultSearchRequest();
        request.setId("abcde");
        assertConstraint(request, Size.class);
    }
}