package com.kakao.pay.repository;

import com.kakao.pay.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository<T extends Payment> extends JpaRepository<T, String> {
}