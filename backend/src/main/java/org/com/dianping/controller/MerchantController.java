package org.com.dianping.controller;

import org.com.dianping.DTO.MerchantResponse;
import org.com.dianping.service.MerchantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/merchants")
public class MerchantController {
    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    @GetMapping("/search")
    public List<MerchantResponse> search(@RequestParam String keyword) {
        return merchantService.searchMerchants(keyword);
    }

    @GetMapping("")
    public List<MerchantResponse> filterAndSort(
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double maxAvgConsume,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "default") String sortBy) {
        return merchantService.filterAndSortMerchants(minScore, maxAvgConsume, type, sortBy);
    }

    @GetMapping("/{id}")
    public MerchantResponse getDetail(@PathVariable Long id) {
        return merchantService.getMerchantDetail(id);
    }
}