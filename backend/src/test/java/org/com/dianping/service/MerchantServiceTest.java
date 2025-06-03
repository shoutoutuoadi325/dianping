package org.com.dianping.service;

import org.com.dianping.entity.Merchant;
import org.com.dianping.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Arrays;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MerchantServiceTest {

    @Mock
    private MerchantRepository merchantRepository;

    private MerchantService merchantService;

    @BeforeEach
    void setUp() {
        merchantService = new MerchantService(merchantRepository);
    }

    @Test
    @DisplayName("Should return merchant when ID exists")
    void getMerchantById_WhenIdExists_ReturnsMerchant() {
        // Arrange
        Long validId = 1L;
        Merchant expectedMerchant = new Merchant();
        expectedMerchant.setId(validId);
        expectedMerchant.setMerchantName("Test Restaurant");
        expectedMerchant.setMerchantNamePinyin("test restaurant");
        expectedMerchant.setCategory("餐馆");
        expectedMerchant.setRating(4.5f);
        expectedMerchant.setAddress("Test Address");
        expectedMerchant.setAvgPrice(50.0f);
        expectedMerchant.setTelephone("123-456-7890");
        expectedMerchant.setBusinessHours("9:00-22:00");
        expectedMerchant.setDescription("Test Description");
        expectedMerchant.setCoverUrl("http://test.com/cover.jpg");
        expectedMerchant.setPhotoUrls(Arrays.asList("http://test.com/photo1.jpg", "http://test.com/photo2.jpg"));

        when(merchantRepository.findById(validId)).thenReturn(Optional.of(expectedMerchant));

        // Act
        Optional<Merchant> result = merchantService.getMerchantById(validId);

        // Assert
        assertTrue(result.isPresent());
        Merchant actualMerchant = result.get();
        assertEquals(expectedMerchant.getId(), actualMerchant.getId());
        assertEquals(expectedMerchant.getMerchantName(), actualMerchant.getMerchantName());
        assertEquals(expectedMerchant.getCategory(), actualMerchant.getCategory());
        assertEquals(expectedMerchant.getRating(), actualMerchant.getRating());
        assertEquals(expectedMerchant.getAddress(), actualMerchant.getAddress());
        assertEquals(expectedMerchant.getAvgPrice(), actualMerchant.getAvgPrice());
        assertEquals(expectedMerchant.getTelephone(), actualMerchant.getTelephone());
        assertEquals(expectedMerchant.getBusinessHours(), actualMerchant.getBusinessHours());
        assertEquals(expectedMerchant.getDescription(), actualMerchant.getDescription());
        assertEquals(expectedMerchant.getCoverUrl(), actualMerchant.getCoverUrl());
        assertEquals(expectedMerchant.getPhotoUrls(), actualMerchant.getPhotoUrls());
        
        verify(merchantRepository, times(1)).findById(validId);
    }

    @Test
    @DisplayName("Should return empty Optional when ID does not exist")
    void getMerchantById_WhenIdDoesNotExist_ReturnsEmpty() {
        // Arrange
        Long invalidId = 999L;
        when(merchantRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act
        Optional<Merchant> result = merchantService.getMerchantById(invalidId);

        // Assert
        assertFalse(result.isPresent());
        verify(merchantRepository, times(1)).findById(invalidId);
    }

    @Test
    @DisplayName("Should handle null ID gracefully")
    void getMerchantById_WhenIdIsNull_ReturnsEmpty() {
        // Arrange
        when(merchantRepository.findById(null)).thenReturn(Optional.empty());

        // Act
        Optional<Merchant> result = merchantService.getMerchantById(null);

        // Assert
        assertFalse(result.isPresent());
        verify(merchantRepository, times(1)).findById(null);
    }

    // Edge case: ID = 0
    @Test
    @DisplayName("Should handle ID = 0")
    void getMerchantById_WhenIdIsZero_ReturnsEmpty() {
        // Arrange
        Long zeroId = 0L;
        when(merchantRepository.findById(zeroId)).thenReturn(Optional.empty());

        // Act
        Optional<Merchant> result = merchantService.getMerchantById(zeroId);

        // Assert
        assertFalse(result.isPresent());
        verify(merchantRepository, times(1)).findById(zeroId);
    }

    // Edge case: Very large ID
    @Test
    @DisplayName("Should handle very large ID")
    void getMerchantById_WhenIdIsVeryLarge_ReturnsEmpty() {
        // Arrange
        Long largeId = Long.MAX_VALUE;
        when(merchantRepository.findById(largeId)).thenReturn(Optional.empty());

        // Act
        Optional<Merchant> result = merchantService.getMerchantById(largeId);

        // Assert
        assertFalse(result.isPresent());
        verify(merchantRepository, times(1)).findById(largeId);
    }
}
