package com.kakao.pay.service.payment;

import com.kakao.pay.exception.ApiException;
import com.kakao.pay.model.payload.PayloadSerializer;
import com.kakao.pay.util.LockerUtil;
import com.kakao.pay.repository.PaymentRepository;
import com.kakao.pay.response.payment.ApplyPaymentResponse;
import com.kakao.pay.constant.ApiError;
import com.kakao.pay.constant.Constants;
import com.kakao.pay.constant.PaymentType;
import com.kakao.pay.entity.payment.ApplyPayment;
import com.kakao.pay.model.payload.Payload;
import com.kakao.pay.request.CardRequest;
import com.kakao.pay.request.payment.ApplyPaymentRequest;
import com.kakao.pay.service.CardService;
import com.kakao.pay.service.SendService;
import com.kakao.pay.util.CardUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.Callable;

@Service
public class ApplyPaymentService {
    @Autowired
    @Qualifier(Constants.PAYLOAD_ID)
    private Callable<String> randomId;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LockerUtil lockerUtil;

    @Autowired
    private CardService cardService;

    @Autowired
    private SendService sendService;

    @Resource
    private PaymentRepository<ApplyPayment> applyPaymentRepository;

    @Transactional
    public ApplyPaymentResponse doWork(ApplyPaymentRequest request) throws ApiException {
        String encryptedCardInfo = cardService.encrypt(request.getCard());

        // 암호화된 카드정보를 레디스에 저장하여 락처리 (중복 처리방지)
        if (!lockerUtil.lock(encryptedCardInfo).tryLock()) {
            throw new ApiException(ApiError.CARD_LOCKED);
        }

        try {
            ApplyPayment applyPayment = modelMapper.map(request, ApplyPayment.class);
            applyPayment.setId(randomId.call());
            applyPayment.setVat(CardUtil.getVat(request));
            applyPayment.setEncryptedCardInfo(encryptedCardInfo);
            applyPayment.setMonth(request.getMonth());
            applyPayment = applyPaymentRepository.save(applyPayment);

            CardRequest cardRequest = request.getCard();
            String data = PayloadSerializer.serialize(Payload
                    .builder()
                    .id(applyPayment.getId())
                    .type(PaymentType.PAYMENT)
                    .paymentMonth(request.getMonth())
                    .paymentPrice(request.getPrice())
                    .encryptedCardInfo(encryptedCardInfo)
                    .vat(request.getVat())
                    .cardNumber(cardRequest.getNumber())
                    .cardExpiryDate(cardRequest.getExpiryDate())
                    .cardVerificationCode(cardRequest.getVerificationCode())
                    .build());

            sendService.send(applyPayment.getId(), data);

            return ApplyPaymentResponse
                    .builder()
                    .id(applyPayment.getId())
                    .data(data)
                    .build();
        } catch (Exception e) {
            throw new ApiException(ApiError.ERROR, e.getLocalizedMessage());
        } finally {
            lockerUtil.unlock();
        }
    }
}