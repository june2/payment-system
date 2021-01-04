package com.kakao.pay.entity.payment;

import com.kakao.pay.constant.PaymentType;
import lombok.Data;

import javax.persistence.*;

import static com.kakao.pay.constant.Constants.ID_SIZE;

@Data
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Entity
public class Payment {
    @Id
    @Column(length = ID_SIZE)
    private String id;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private Long vat;

    @Column(nullable = false)
    private String month;

    @Column
    private String encryptedCardInfo;

    public PaymentType getType() {
        return PaymentType.valueOf(
                getClass()
                        .getAnnotation(DiscriminatorValue.class)
                        .value()
        );
    }
}