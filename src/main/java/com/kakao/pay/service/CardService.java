package com.kakao.pay.service;

import com.kakao.pay.util.CardUtil;
import com.kakao.pay.constant.ApiError;
import com.kakao.pay.util.CrtyptoUtil;
import com.kakao.pay.exception.ApiException;
import com.kakao.pay.model.card.CardInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public class CardService {
    @Value("${card.cipher.type}")
    private String CIPHER_TYPE;

    @Value("${card.cipher.key}")
    private String CIPHER_KEY;

    @Value("${card.cipher.separator}")
    private String CIPHER_SEPARATOR;

    private CrtyptoUtil crtyptoUtil;

    @PostConstruct
    public void init() throws NoSuchAlgorithmException, InvalidKeySpecException {
        crtyptoUtil = new CrtyptoUtil(CIPHER_TYPE, CIPHER_KEY);
    }

    public String encrypt(CardInfo cardInfo) throws ApiException {
        try {
            return crtyptoUtil.encrypt(CardUtil.serialize(cardInfo));
        } catch (Exception e) {
            throw new ApiException(ApiError.ERROR, e.getMessage());
        }
    }

    public CardInfo decrypt(String encryptedCardInfo) throws ApiException {
        try {
            return CardUtil.deserialize(crtyptoUtil.decrypt(encryptedCardInfo));
        } catch (Exception e) {
            throw new ApiException(ApiError.ERROR, e.getMessage());
        }
    }
}