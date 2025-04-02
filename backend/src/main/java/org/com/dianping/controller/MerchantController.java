package org.com.dianping.controller;

import org.com.dianping.entity.Merchant;
import org.com.dianping.repository.MerchantRepository;
import org.com.dianping.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @Autowired
    private MerchantRepository merchantRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        return merchantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Merchant> getBusinesses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Float rating,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) Float avgPrice,
            @RequestParam(defaultValue = "default") String sort) {

        // 处理价格区间
        Float minPrice = null;
        Float maxPrice = null;
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] parts = priceRange.split("-");
            if (parts.length == 2) {
                minPrice = Float.parseFloat(parts[0]);
                maxPrice = Float.parseFloat(parts[1]);
            } else if (priceRange.endsWith("-")) {
                minPrice = Float.parseFloat(priceRange.substring(0, priceRange.length() - 1));
            } else if (priceRange.startsWith("-")) {
                maxPrice = Float.parseFloat(priceRange.substring(1));
            } else {
                minPrice = Float.parseFloat(priceRange);
            }
        }
        if (avgPrice != null) {
            maxPrice = avgPrice;
        }
        return merchantService.getMerchants(
                keyword,
                rating,
                minPrice,
                maxPrice,
                sort.equals("default") ? null : sort
        );
    }
}