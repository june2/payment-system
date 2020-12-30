package com.kakao.pay.repository;

import com.kakao.pay.entity.SendPayload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendPayloadRepository extends JpaRepository<SendPayload, String> {
}