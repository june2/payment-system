package com.kakao.pay.repository;

import com.kakao.pay.entity.Payload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayloadRepository extends JpaRepository<Payload, String> {
}