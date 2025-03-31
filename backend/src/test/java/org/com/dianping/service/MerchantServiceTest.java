package org.com.dianping.service;

import org.com.dianping.DTO.MerchantResponse;
import org.com.dianping.entity.Merchant;
import org.com.dianping.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    @InjectMocks
    private MerchantService merchantService;

    @Captor
    private ArgumentCaptor<Specification<Merchant>> specCaptor;

    @Captor
    private ArgumentCaptor<Sort> sortCaptor;

    private Merchant merchant1;
    private Merchant merchant2;

    @BeforeEach
    void setUp() {
        merchant1 = new Merchant(
                1L, "Restaurant A", "Restaurant", 4.5, "Location A",
                80.0, 12345678L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Chinese, Spicy", "url1");

        merchant2 = new Merchant(
                2L, "Coffee Shop B", "Cafe", 4.2, "Location B",
                40.0, 87654321L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Coffee, Dessert", "url2");
    }

    @Test
    void searchMerchants_ShouldReturnMerchantResponses() {
        String keyword = "restaurant";
        when(merchantRepository.fuzzySearch(keyword)).thenReturn(Arrays.asList(merchant1));

        List<MerchantResponse> result = merchantService.searchMerchants(keyword);

        assertEquals(1, result.size());
        assertEquals(merchant1.getId(), result.get(0).id());
        assertEquals(merchant1.getMerchantName(), result.get(0).merchantName());
        verify(merchantRepository).fuzzySearch(keyword);
    }

    @Test
    void filterAndSortMerchants_WithDefaultSorting() {
        when(merchantRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(Arrays.asList(merchant1, merchant2));

        List<MerchantResponse> result = merchantService.filterAndSortMerchants(null, null, null, "default");

        assertEquals(2, result.size());
        verify(merchantRepository).findAll(specCaptor.capture(), sortCaptor.capture());
        assertEquals(Sort.unsorted(), sortCaptor.getValue());
    }

    @Test
    void filterAndSortMerchants_SortByScore() {
        when(merchantRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(Arrays.asList(merchant1, merchant2));

        List<MerchantResponse> result = merchantService.filterAndSortMerchants(null, null, null, "score");

        assertEquals(2, result.size());
        verify(merchantRepository).findAll(specCaptor.capture(), sortCaptor.capture());
        assertEquals(Sort.Direction.DESC, sortCaptor.getValue().getOrderFor("score").getDirection());
    }

    @Test
    void filterAndSortMerchants_SortByConsumption() {
        when(merchantRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(Arrays.asList(merchant2, merchant1));

        List<MerchantResponse> result = merchantService.filterAndSortMerchants(null, null, null, "consumption");

        assertEquals(2, result.size());
        verify(merchantRepository).findAll(specCaptor.capture(), sortCaptor.capture());
        assertEquals(Sort.Direction.ASC, sortCaptor.getValue().getOrderFor("averageConsumption").getDirection());
    }

    @Test
    void filterAndSortMerchants_WithFilters() {
        Double minScore = 4.0;
        Double maxAvgConsume = 100.0;
        String type = "Restaurant";

        when(merchantRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(Arrays.asList(merchant1));

        List<MerchantResponse> result = merchantService.filterAndSortMerchants(minScore, maxAvgConsume, type,
                "default");

        assertEquals(1, result.size());
        assertEquals(merchant1.getId(), result.get(0).id());
        verify(merchantRepository).findAll(specCaptor.capture(), any(Sort.class));
    }

    @Test
    void getMerchantDetail_ShouldReturnMerchantResponse_WhenMerchantExists() {
        Long id = 1L;
        when(merchantRepository.findById(id)).thenReturn(Optional.of(merchant1));

        MerchantResponse result = merchantService.getMerchantDetail(id);

        assertNotNull(result);
        assertEquals(merchant1.getId(), result.id());
        assertEquals(merchant1.getMerchantName(), result.merchantName());
        verify(merchantRepository).findById(id);
    }

    @Test
    void getMerchantDetail_ShouldThrowException_WhenMerchantDoesNotExist() {
        Long id = 99L;
        when(merchantRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> merchantService.getMerchantDetail(id));

        assertEquals("Merchant not found", exception.getMessage());
        verify(merchantRepository).findById(id);
    }
}
