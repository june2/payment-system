package com.kakao.pay.service;

import com.kakao.pay.entity.Payload;
import com.kakao.pay.repository.PayloadRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SendService {
    @Resource
    private PayloadRepository payloadRepository;

    public Payload send(String id, String data) {
        // 카드사 정보 전송후, DB저장
        return payloadRepository.save(
                Payload.builder().id(id).data(data).build());
    }
}