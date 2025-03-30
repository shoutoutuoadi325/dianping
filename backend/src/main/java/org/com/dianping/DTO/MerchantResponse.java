package org.com.dianping.DTO;

import java.sql.Date;
import org.com.dianping.entity.Merchant;

public record MerchantResponse(
        long id,
        String merchantName,
        String type,
        double score,
        String location,
        double averageConsumption,
        long telephoneNumber,
        Date startTime,
        Date endTime,
        String tags,
        String photoUrl
) {
    public MerchantResponse(Merchant merchant) {
        this(merchant.getId(),
                merchant.getMerchantName(),
                merchant.getType(),
                merchant.getScore(),
                merchant.getLocation(),
                merchant.getAverageConsumption(),
                merchant.getTelephoneNumber(),
                merchant.getStartTime(),
                merchant.getEndTime(),
                merchant.getTags(),
                merchant.getPhotoUrl());
    }
}