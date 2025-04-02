package org.com.dianping.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.com.dianping.DTO.MerchantResponse;
import org.com.dianping.entity.Merchant;
import org.com.dianping.service.MerchantService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchants")
public class MerchantController {

    private final MerchantService merchantService;

    public MerchantController(MerchantService merchantService) {
        this.merchantService = merchantService;
    }

    // 新增商户
    @PostMapping
    public MerchantResponse addMerchant(@RequestBody Merchant merchant) {
        Merchant savedMerchant = merchantService.saveMerchant(merchant);
        return new MerchantResponse(savedMerchant);
    }

    // 获取商户详情
    @GetMapping("/{id}")
    public MerchantResponse getMerchantById(@PathVariable long id) {
        Merchant merchant = merchantService.getMerchantById(id);
        return new MerchantResponse(merchant);
    }

    // 获取所有商户
    @GetMapping
    public List<MerchantResponse> getAllMerchants() {
        List<Merchant> merchants = merchantService.getAllMerchants();
        return merchants.stream()
                .map(MerchantResponse::new)
                .collect(Collectors.toList());
    }

    // 更新商户信息
    @PutMapping("/{id}")
    public MerchantResponse updateMerchant(@PathVariable long id, @RequestBody Merchant merchant) {
        Merchant updatedMerchant = merchantService.updateMerchant(id, merchant);
        return new MerchantResponse(updatedMerchant);
    }

    // 删除商户
    @DeleteMapping("/{id}")
    public void deleteMerchant(@PathVariable long id) {
        merchantService.deleteMerchant(id);
    }

    // 新增：多条件筛选商户
    @GetMapping("/filter")
    public List<MerchantResponse> filterMerchants(
            @RequestParam(required = false) Double minScore,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) List<String> types) {
        List<Merchant> filtered = merchantService.filterMerchants(minScore, minPrice, maxPrice, types);
        return filtered.stream().map(MerchantResponse::new).collect(Collectors.toList());
    }
}