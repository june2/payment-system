package com.kakao.pay.entity;

import com.kakao.pay.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payload {
    @Id
    @Column(length = Constants.ID_SIZE)
    private String id;

    @Column(length = Constants.PAYLOAD_SIZE, nullable = false)
    private String data;
}