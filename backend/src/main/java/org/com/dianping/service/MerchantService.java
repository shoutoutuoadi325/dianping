package org.com.dianping.service;

import jakarta.transaction.Transactional;
import org.com.dianping.entity.Merchant;
import org.com.dianping.repository.MerchantRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }
    @Transactional()
    public List<Merchant> getMerchants(String keyword, Float minRating,
                                       Float minPrice, Float maxPrice,
                                       String sortType) {
        List<Merchant> results = merchantRepository.searchMerchants(
                keyword, minRating, minPrice, maxPrice);

        // 应用排序
        if (sortType != null) {
            switch (sortType) {
                case "rating":
                    results.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
                    break;
                case "price_asc":
                    results.sort((a, b) -> Float.compare(a.getAvgPrice(), b.getAvgPrice()));
                    break;
                case "price_desc":
                    results.sort((a, b) -> Float.compare(b.getAvgPrice(), a.getAvgPrice()));
                    break;
            }
        }
        return results;
    }
}