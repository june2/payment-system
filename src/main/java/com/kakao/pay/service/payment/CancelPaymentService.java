package com.kakao.pay.service.payment;

import com.kakao.pay.model.card.CardInfo;
import com.kakao.pay.entity.payment.CancelPayment;
import com.kakao.pay.exception.ApiException;
import com.kakao.pay.model.payload.PayloadSerializer;
import com.kakao.pay.repository.PaymentRepository;
import com.kakao.pay.response.payment.CancelPaymentResponse;
import com.kakao.pay.constant.ApiError;
import com.kakao.pay.constant.Constants;
import com.kakao.pay.constant.PaymentType;
import com.kakao.pay.entity.payment.ApplyPayment;
import com.kakao.pay.model.payload.Payload;
import com.kakao.pay.request.payment.CancelPaymentRequest;
import com.kakao.pay.service.CardService;
import com.kakao.pay.service.SendService;
import com.kakao.pay.util.LockerUtil;
import lombok.Data;
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
public class CancelPaymentService {
    @Autowired
    @Qualifier(Constants.PAYLOAD_ID)
    private Callable<String> randomId;

    @Autowired
    private LockerUtil lockerUtil;

    @Autowired
    private CardService cardService;

    @Autowired
    private SendService sendService;

    @Resource
    private PaymentRepository<ApplyPayment> applyPaymentRepository;

    @Transactional
    public CancelPaymentResponse doWork(CancelPaymentRequest request) throws ApiException {

        if (!lockerUtil.lock(request.getPaymentId()).tryLock()) {
            throw new ApiException(ApiError.PAYMENT_LOCKED);
        }

        ApplyPayment applyPayment = applyPaymentRepository
                .findById(request.getPaymentId())
                .orElseThrow(() -> new ApiException(ApiError.PAYMENT_NOT_FOUND));

        ValidResult validResult = validate(applyPayment, request);

        CardInfo cardInfo = cardService.decrypt(applyPayment.getEncryptedCardInfo());

        try {
            long requireVat = validResult.getRequireVat();

            CancelPayment cancelPayment = new CancelPayment();
            cancelPayment.setId(randomId.call());
            cancelPayment.setPrice(request.getPrice());
            cancelPayment.setVat(requireVat);
            cancelPayment.setEncryptedCardInfo(applyPayment.getEncryptedCardInfo());
            applyPayment.addCancelPayment(cancelPayment);
            applyPaymentRepository.save(applyPayment);

            String data = PayloadSerializer.serialize(Payload
                    .builder()
                    .id(cancelPayment.getId())
                    .type(PaymentType.CANCEL)
                    .paymentId(request.getPaymentId())
                    .paymentMonths(0)
                    .paymentPrice(request.getPrice())
                    .encryptedCardInfo(applyPayment.getEncryptedCardInfo())
                    .vat(requireVat)
                    .cardNumber(cardInfo.getNumber())
                    .cardExpiryDate(cardInfo.getExpiryDate())
                    .cardVerificationCode(cardInfo.getVerificationCode())
                    .build());
            sendService.send(cancelPayment.getId(), data);

            return CancelPaymentResponse
                    .builder()
                    .id(cancelPayment.getId())
                    .build();
        } catch (Exception e) {
            throw new ApiException(ApiError.ERROR, e.getLocalizedMessage());
        } finally {
            lockerUtil.unlock();
        }
    }

    private ValidResult validate(ApplyPayment applyPayment, CancelPaymentRequest request) throws ApiException {
        long remainingPrice = applyPayment.getRemainingPrice();
        long remainingVat = applyPayment.getRemainingVat();

        long requirePrice = request.getPrice();
        long requireVat = Optional
                .ofNullable(request.getVat())
                .orElse(Math.min(remainingVat, request.getDefaultVat()));

        if (requirePrice > remainingPrice) {
            throw new ApiException(ApiError.NOT_ENOUGH_PRICE);
        }

        if (requireVat > remainingVat) {
            throw new ApiException(ApiError.NOT_ENOUGH_VAT);
        }

        if (remainingPrice - requirePrice < remainingVat - requireVat) {
            throw new ApiException(ApiError.VAT_GREATER_THAN_PRICE);
        }

        return new ValidResult(requireVat);
    }

    @Data
    private static class ValidResult {
        private long requireVat;

        ValidResult(long requireVat) {
            this.requireVat = requireVat;
        }
    }
}