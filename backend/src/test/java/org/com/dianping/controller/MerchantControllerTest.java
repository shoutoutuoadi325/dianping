package org.com.dianping.controller;

import org.com.dianping.DTO.MerchantResponse;
import org.com.dianping.service.MerchantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MerchantController.class)
class MerchantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MerchantService merchantService;

    @Test
    void search_ShouldReturnMerchantsList_WhenKeywordProvided() throws Exception {
        String keyword = "restaurant";
        MerchantResponse merchant = new MerchantResponse(
                1L, "Restaurant A", "Restaurant", 4.5, "Location A",
                80.0, 12345678L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Chinese, Spicy", "url1");

        when(merchantService.searchMerchants(keyword)).thenReturn(Collections.singletonList(merchant));

        mockMvc.perform(get("/merchants/search")
                .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].merchantName", is("Restaurant A")));

        verify(merchantService).searchMerchants(keyword);
    }

    @Test
    void filterAndSort_ShouldReturnMerchantsList_WithDefaultSorting() throws Exception {
        MerchantResponse merchant1 = new MerchantResponse(
                1L, "Restaurant A", "Restaurant", 4.5, "Location A",
                80.0, 12345678L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Chinese, Spicy", "url1");
        MerchantResponse merchant2 = new MerchantResponse(
                2L, "Coffee Shop B", "Cafe", 4.2, "Location B",
                40.0, 87654321L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Coffee, Dessert", "url2");

        when(merchantService.filterAndSortMerchants(null, null, null, "default"))
                .thenReturn(Arrays.asList(merchant1, merchant2));

        mockMvc.perform(get("/merchants"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(merchantService).filterAndSortMerchants(null, null, null, "default");
    }

    @Test
    void filterAndSort_ShouldReturnMerchantsList_WithFiltersAndSorting() throws Exception {
        Double minScore = 4.0;
        Double maxAvgConsume = 100.0;
        String type = "Restaurant";
        String sortBy = "score";

        MerchantResponse merchant = new MerchantResponse(
                1L, "Restaurant A", "Restaurant", 4.5, "Location A",
                80.0, 12345678L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Chinese, Spicy", "url1");

        when(merchantService.filterAndSortMerchants(minScore, maxAvgConsume, type, sortBy))
                .thenReturn(Collections.singletonList(merchant));

        mockMvc.perform(get("/merchants")
                .param("minScore", minScore.toString())
                .param("maxAvgConsume", maxAvgConsume.toString())
                .param("type", type)
                .param("sortBy", sortBy))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].merchantName", is("Restaurant A")));

        verify(merchantService).filterAndSortMerchants(minScore, maxAvgConsume, type, sortBy);
    }

    @Test
    void getDetail_ShouldReturnMerchantResponse_WhenMerchantExists() throws Exception {
        Long id = 1L;
        MerchantResponse merchant = new MerchantResponse(
                id, "Restaurant A", "Restaurant", 4.5, "Location A",
                80.0, 12345678L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Chinese, Spicy", "url1");

        when(merchantService.getMerchantDetail(id)).thenReturn(merchant);

        mockMvc.perform(get("/merchants/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.merchantName", is("Restaurant A")));

        verify(merchantService).getMerchantDetail(id);
    }

    @Test
    void getDetail_ShouldReturnBadRequest_WhenMerchantDoesNotExist() throws Exception {
        Long id = 99L;

        when(merchantService.getMerchantDetail(id)).thenThrow(new RuntimeException("Merchant not found"));

        mockMvc.perform(get("/merchants/{id}", id))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Merchant not found")));

        verify(merchantService).getMerchantDetail(id);
    }
}
