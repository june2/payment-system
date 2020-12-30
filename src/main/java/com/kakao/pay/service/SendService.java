package com.kakao.pay.service;

import com.kakao.pay.entity.SendPayload;
import com.kakao.pay.repository.SendPayloadRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SendService {
    @Resource
    private SendPayloadRepository sendPayloadRepository;

    public SendPayload send(String id, String data) {
        return sendPayloadRepository.save(
                SendPayload.builder().id(id).data(data).build());
    }
}