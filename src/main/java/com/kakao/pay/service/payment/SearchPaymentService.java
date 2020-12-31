package com.kakao.pay.service.payment;

import com.kakao.pay.model.card.CardInfo;
import com.kakao.pay.model.card.MaskedCardInfo;
import com.kakao.pay.entity.payment.Payment;
import com.kakao.pay.exception.ApiException;
import com.kakao.pay.repository.PaymentRepository;
import com.kakao.pay.constant.ApiError;
import com.kakao.pay.entity.payment.ApplyPayment;
import com.kakao.pay.request.payment.SearchPaymentRequest;
import com.kakao.pay.response.payment.SearchPaymentResponse;
import com.kakao.pay.service.CardService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Service
public class SearchPaymentService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CardService cardService;

    @Resource
    private PaymentRepository<Payment> paymentRepository;

    private ResponseFactory responseFactory;

    @PostConstruct
    void init() {
        responseFactory = new ResponseFactory();
    }

    public SearchPaymentResponse doWork(SearchPaymentRequest request) throws ApiException {
        Payment payment = paymentRepository
                .findById(request.getId())
                .orElseThrow(() -> new ApiException(ApiError.PAYMENT_NOT_FOUND));

        CardInfo cardInfo = cardService.decrypt(payment.getEncryptedCardInfo());

        if (payment instanceof ApplyPayment) {
            return responseFactory.ofApplyPayment((ApplyPayment) payment, cardInfo);
        } else {
            return responseFactory.ofDefault(payment, cardInfo);
        }
    }

    private class ResponseFactory {
        SearchPaymentResponse ofApplyPayment(ApplyPayment payment, CardInfo cardInfo) {
            return SearchPaymentResponse
                    .builder()
                    .id(payment.getId())
                    .cardInfo(modelMapper.map(cardInfo, MaskedCardInfo.class))
                    .paymentType(payment.getType())
                    .price(payment.getRemainingPrice())
                    .vat(payment.getRemainingVat())
                    .build();
        }

        SearchPaymentResponse ofDefault(Payment payment, CardInfo cardInfo) {
            return SearchPaymentResponse
                    .builder()
                    .id(payment.getId())
                    .cardInfo(modelMapper.map(cardInfo, MaskedCardInfo.class))
                    .paymentType(payment.getType())
                    .price(payment.getPrice())
                    .vat(payment.getVat())
                    .build();
        }
    }
}