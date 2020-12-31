package com.kakao.pay.util;

import com.kakao.pay.common.CommonTestCase;
import com.kakao.pay.exception.ApiException;
import com.kakao.pay.model.card.CardInfo;
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
}