package com.kakao.pay.request.payment;

import com.kakao.pay.validator.PriceGreaterThanVat;
import com.kakao.pay.common.CommonTestCase;
import org.junit.Test;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class ApplyPaymentRequestTest extends CommonTestCase {
    @Test
    public void 카드_정보_누락() {
        ApplyPaymentRequest request = defaultApplyRequest();
        request.setCard(null);
        assertConstraint(request, NotNull.class);
    }

    @Test
    public void 할부개월_누락() {
        ApplyPaymentRequest request = defaultApplyRequest();
        request.setMonth(null);
        assertConstraint(request, NotNull.class);
    }

    @Test
    public void 할부개월_범위_미만() {
        ApplyPaymentRequest request = defaultApplyRequest();
        request.setMonth(-1);
        assertConstraint(request, Min.class);
    }

    @Test
    public void 할부개월_범위_초과() {
        ApplyPaymentRequest request = defaultApplyRequest();
        request.setMonth(20);
        assertConstraint(request, Max.class);
    }

    @Test
    public void 금액_누락() {
        ApplyPaymentRequest request = defaultApplyRequest();
        request.setPrice(null);
        assertConstraint(request, NotNull.class);
    }

    @Test
    public void 금액_범위_미만() {
        ApplyPaymentRequest request = defaultApplyRequest();
        request.setPrice(10L);
        request.setVat(1L);
        assertConstraint(request, Min.class);
    }

    @Test
    public void 금액_범위_초과() {
        ApplyPaymentRequest request = defaultApplyRequest();
        request.setPrice(5000000000L);
        assertConstraint(request, Max.class);
    }

    @Test
    public void VAT가_금액보다_많은_경우() {
        ApplyPaymentRequest request = defaultApplyRequest();
        request.setPrice(100L);
        request.setVat(10000L);
        assertConstraint(request, PriceGreaterThanVat.class);
    }
}