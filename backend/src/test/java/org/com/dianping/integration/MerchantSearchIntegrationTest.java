package org.com.dianping.integration;

import org.com.dianping.entity.Merchant;
import org.com.dianping.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MerchantSearchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MerchantRepository merchantRepository;

    private Merchant merchant1;
    private Merchant merchant2;
    private Merchant merchant3;

    @BeforeEach
    void setUp() {
        // 清除所有现有商户数据
        merchantRepository.deleteAll();

        // 创建测试商户数据
        merchant1 = new Merchant(
                0L, "Fine Restaurant", "Restaurant", 4.8, "Location A",
                120.0, 12345678L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Fine Dining, Exclusive", "url1");

        merchant2 = new Merchant(
                0L, "Casual Cafe", "Cafe", 4.2, "Location B",
                50.0, 87654321L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Coffee, Dessert", "url2");

        merchant3 = new Merchant(
                0L, "Budget Restaurant", "Restaurant", 3.9, "Location C",
                25.0, 13579246L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Fast Food, Budget", "url3");

        // 保存到测试数据库
        merchant1 = merchantRepository.save(merchant1);
        merchant2 = merchantRepository.save(merchant2);
        merchant3 = merchantRepository.save(merchant3);
    }

    @Test
    void searchMerchants_ShouldReturnMatchingResults() throws Exception {
        mockMvc.perform(get("/merchants/search")
                .param("keyword", "restaurant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].merchantName", hasItems("Fine Restaurant", "Budget Restaurant")));
    }

    @Test
    void filterMerchants_ByMinScore() throws Exception {
        mockMvc.perform(get("/merchants")
                .param("minScore", "4.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].merchantName", is("Fine Restaurant")));
    }

    @Test
    void filterMerchants_ByMaxAvgConsume() throws Exception {
        mockMvc.perform(get("/merchants")
                .param("maxAvgConsume", "60.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].merchantName", hasItems("Casual Cafe", "Budget Restaurant")));
    }

    @Test
    void filterMerchants_ByType() throws Exception {
        mockMvc.perform(get("/merchants")
                .param("type", "Cafe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].merchantName", is("Casual Cafe")));
    }

    @Test
    void sortMerchants_ByScore() throws Exception {
        mockMvc.perform(get("/merchants")
                .param("sortBy", "score"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                // 按分数降序
                .andExpect(jsonPath("$[0].merchantName", is("Fine Restaurant")))
                .andExpect(jsonPath("$[1].merchantName", is("Casual Cafe")))
                .andExpect(jsonPath("$[2].merchantName", is("Budget Restaurant")));
    }

    @Test
    void sortMerchants_ByConsumption() throws Exception {
        mockMvc.perform(get("/merchants")
                .param("sortBy", "consumption"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                // 按消费升序
                .andExpect(jsonPath("$[0].merchantName", is("Budget Restaurant")))
                .andExpect(jsonPath("$[1].merchantName", is("Casual Cafe")))
                .andExpect(jsonPath("$[2].merchantName", is("Fine Restaurant")));
    }

    @Test
    void getDetail_ShouldReturnMerchantDetails() throws Exception {
        mockMvc.perform(get("/merchants/{id}", merchant1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.merchantName", is("Fine Restaurant")))
                .andExpect(jsonPath("$.type", is("Restaurant")))
                .andExpect(jsonPath("$.score", is(4.8)))
                .andExpect(jsonPath("$.location", is("Location A")))
                .andExpect(jsonPath("$.averageConsumption", is(120.0)))
                .andExpect(jsonPath("$.telephoneNumber", is(12345678)));
    }

    @Test
    void getDetail_ShouldReturnNotFound_ForNonExistentMerchant() throws Exception {
        mockMvc.perform(get("/merchants/{id}", 9999L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Merchant not found")));
    }
}
