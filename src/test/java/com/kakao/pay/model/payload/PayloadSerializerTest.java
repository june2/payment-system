package com.kakao.pay.model.payload;

import com.kakao.pay.common.CommonTestCase;
import com.kakao.pay.constant.Constants;
import com.kakao.pay.constant.PaymentType;
import com.kakao.pay.model.card.CardInfo;
import com.kakao.pay.request.payment.ApplyPaymentRequest;
import com.kakao.pay.service.CardService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;

public class PayloadSerializerTest extends CommonTestCase {

    @Autowired
    @Qualifier(Constants.PAYLOAD_ID)
    private Callable<String> randomId;

    @Autowired
    private CardService cardService;

    @Test
    public void 카드_string_data() throws Exception {
        ApplyPaymentRequest request = defaultApplyRequest();
        CardInfo cardRequest = request.getCard();
        String encryptedCardInfo = cardService.encrypt(request.getCard());

        String data = PayloadSerializer.serialize(Payload
                .builder()
                .id(randomId.call())
                .type(PaymentType.PAYMENT)
                .paymentMonth(request.getMonth())
                .paymentPrice(request.getPrice())
                .encryptedCardInfo(encryptedCardInfo)
                .vat(request.getVat())
                .cardNumber(cardRequest.getNumber())
                .cardExpiryDate(cardRequest.getExpiryDate())
                .cardVerificationCode(cardRequest.getVerificationCode())
                .build());

        assertEquals(data.length(), 450);
    }
}