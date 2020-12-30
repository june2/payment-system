package com.kakao.pay.service.payment;

import com.kakao.pay.exception.ApiException;
import com.kakao.pay.payload.PayloadSerializer;
import com.kakao.pay.repository.PaymentRepository;
import com.kakao.pay.response.payment.ApplyPaymentResponse;
import com.kakao.pay.constant.ApiError;
import com.kakao.pay.constant.Constants;
import com.kakao.pay.constant.PaymentType;
import com.kakao.pay.entity.payment.ApplyPayment;
import com.kakao.pay.payload.Payload;
import com.kakao.pay.request.CardRequest;
import com.kakao.pay.request.payment.ApplyPaymentRequest;
import com.kakao.pay.service.CardService;
import com.kakao.pay.service.SendService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

@Service
public class ApplyPaymentService {
    @Autowired
    @Qualifier(Constants.PAYLOAD_ID)
    private Callable<String> randomId;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LockRegistry lockRegistry;

    @Autowired
    private CardService cardService;

    @Autowired
    private SendService sendService;

    @Resource
    private PaymentRepository<ApplyPayment> applyPaymentRepository;

    @Transactional
    public ApplyPaymentResponse doWork(ApplyPaymentRequest request) throws ApiException {
        String encryptedCardInfo = cardService.encrypt(request.getCard());

        Lock lock = lockCardInfo(encryptedCardInfo);

        try {
            ApplyPayment applyPayment = modelMapper.map(request, ApplyPayment.class);
            applyPayment.setId(randomId.call());
            applyPayment.setVat(getActualVat(request));
            applyPayment.setEncryptedCardInfo(encryptedCardInfo);
            applyPayment = applyPaymentRepository.save(applyPayment);

            CardRequest cardRequest = request.getCard();
            String data = PayloadSerializer.serialize(Payload
                    .builder()
                    .id(applyPayment.getId())
                    .type(PaymentType.PAYMENT)
                    .paymentMonths(request.getMonths())
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
            lock.unlock();
        }
    }

    private Long getActualVat(ApplyPaymentRequest request) {
        if (request.getPrice() != null) {
            return Optional
                    .ofNullable(request.getVat())
                    .orElse(request.getDefaultVat());
        } else {
            return request.getVat();
        }
    }

    private Lock lockCardInfo(String encryptedCardInfo) throws ApiException {
        Lock lock = lockRegistry.obtain(encryptedCardInfo);

        if (lock.tryLock()) {
            return lock;
        } else {
            throw new ApiException(ApiError.CARD_LOCKED);
        }
    }
}