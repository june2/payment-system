package com.kakao.pay.model.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kakao.pay.model.card.CardInfo;
import org.apache.commons.lang3.StringUtils;

public class MaskedCardInfo extends CardInfo {
    @JsonIgnore
    private static final String MASK_CHAR = "*";

    @Override
    public void setNumber(String number) {
        int start = 3;
        int end = Math.min(number.length(), start + (number.length() - 6));

        super.setNumber(
                new StringBuilder()
                        .append(StringUtils.repeat(MASK_CHAR, 3))
                        .append(StringUtils.substring(number, start, end))
                        .append(StringUtils.repeat(MASK_CHAR, 3))
                        .toString()
        );
    }
}