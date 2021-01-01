package com.kakao.pay.util;

import com.kakao.pay.common.CommonTestCase;
import com.kakao.pay.exception.ApiException;
import com.kakao.pay.model.card.CardInfo;
import com.kakao.pay.request.payment.ApplyPaymentRequest;
import com.kakao.pay.service.CardService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class CardUtilTest extends CommonTestCase {

    @Autowired
    CardService cardService;

    @Test
    public void 카드_암호화_복호화() throws ApiException {
        String str = cardService.encrypt(defaultCardRequest());
        CardInfo info = cardService.decrypt(str);
        assertEquals(info, defaultCardRequest());
    }

    @Test
    public void 카드_부가가치세() {
        ApplyPaymentRequest request = defaultApplyRequest();

        request.setPrice(1000L);
        request.setVat(null);
        Long vat = CardUtil.getVat(request);
        assertEquals(java.util.Optional.ofNullable(vat).get().longValue(), 91L);

        request.setVat(0L);
        vat = CardUtil.getVat(request);
        assertEquals(java.util.Optional.ofNullable(vat).get().longValue(), 0L);
    }
}