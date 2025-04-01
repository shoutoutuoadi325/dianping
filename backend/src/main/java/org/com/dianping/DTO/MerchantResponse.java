package org.com.dianping.DTO;

import org.com.dianping.entity.Merchant;

public record MerchantResponse(
        long id,
        String merchantName,
        String type,
        float score,
        String location,
        float averageConsumption,
        String tags,
        String photoUrl
) {
    public MerchantResponse(Merchant merchant) {
        this(
            merchant.getId(),
            merchant.getMerchantName(),
            merchant.getType(),
            merchant.getScore(),
            merchant.getLocation(),
            merchant.getAverageConsumption(),
            merchant.getTags(),
            merchant.getPhotoUrl()
        );
    }
}