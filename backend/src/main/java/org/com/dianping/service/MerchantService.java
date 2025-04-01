package org.com.dianping.service;

import org.com.dianping.entity.Merchant;
import org.com.dianping.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    // 保存商户
    public Merchant saveMerchant(Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    // 根据 ID 获取商户
    public Merchant getMerchantById(long id) {
        Optional<Merchant> merchant = merchantRepository.findById(id);
        return merchant.orElseThrow(() -> new RuntimeException("Merchant not found with id: " + id));
    }

    // 获取所有商户
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    // 更新商户信息
    public Merchant updateMerchant(long id, Merchant merchant) {
        Merchant existingMerchant = getMerchantById(id);
        existingMerchant.setMerchantName(merchant.getMerchantName());
        existingMerchant.setType(merchant.getType());
        existingMerchant.setScore(merchant.getScore());
        existingMerchant.setLocation(merchant.getLocation());
        existingMerchant.setAverageConsumption(merchant.getAverageConsumption());
        existingMerchant.setTelephoneNumber(merchant.getTelephoneNumber());
        existingMerchant.setStartTime(merchant.getStartTime());
        existingMerchant.setEndTime(merchant.getEndTime());
        existingMerchant.setTags(merchant.getTags());
        existingMerchant.setPhotoUrl(merchant.getPhotoUrl());
        return merchantRepository.save(existingMerchant);
    }

    // 删除商户
    public void deleteMerchant(long id) {
        merchantRepository.deleteById(id);
    }
}