package org.com.dianping.service;

import jakarta.persistence.criteria.Predicate;
import org.com.dianping.DTO.MerchantResponse;
import org.com.dianping.entity.Merchant;
import org.com.dianping.repository.MerchantRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public List<MerchantResponse> searchMerchants(String keyword) {
        return merchantRepository.fuzzySearch(keyword)
                .stream()
                .map(MerchantResponse::new)
                .collect(Collectors.toList());
    }

    public List<MerchantResponse> filterAndSortMerchants(Double minScore, Double maxAvgConsume, String type, String sortBy) {
        Specification<Merchant> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minScore != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("score"), minScore));
            }
            if (maxAvgConsume != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("averageConsumption"), maxAvgConsume));
            }
            if (type != null && !type.isEmpty()) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = switch (sortBy) {
            case "score" -> Sort.by(Sort.Direction.DESC, "score");
            case "consumption" -> Sort.by(Sort.Direction.ASC, "averageConsumption");
            default -> Sort.unsorted();
        };

        return merchantRepository.findAll(spec, sort)
                .stream()
                .map(MerchantResponse::new)
                .collect(Collectors.toList());
    }

    public MerchantResponse getMerchantDetail(Long id) {
        return merchantRepository.findById(id)
                .map(MerchantResponse::new)
                .orElseThrow(() -> new RuntimeException("Merchant not found"));
    }
}