package org.com.dianping.controller;

import org.com.dianping.entity.Merchant;
import org.com.dianping.service.MerchantService;
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

    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        return merchantService.getMerchantById(id)
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
        return merchantService.getMerchants(keyword, rating, priceRange, avgPrice, sort);
    }

    @GetMapping("/search")
    public List<Merchant> searchMerchants(@RequestParam String keyword) {
        return merchantService.searchMerchantsByKeyword(keyword);
    }
}