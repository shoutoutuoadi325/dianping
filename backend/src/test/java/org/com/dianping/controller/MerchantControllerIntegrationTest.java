package org.com.dianping.controller;

import org.com.dianping.entity.Merchant;
import org.com.dianping.repository.MerchantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MerchantControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MerchantRepository merchantRepository;

    private Merchant savedMerchant;

    @BeforeEach
    void setUp() {
        merchantRepository.deleteAll();
        Merchant merchant = new Merchant();
        merchant.setMerchantName("Test Restaurant");
        merchant.setMerchantNamePinyin("testrestaurant");
        merchant.setCategory("餐馆");
        merchant.setRating(4.5f);
        merchant.setAddress("Test Address");
        merchant.setAvgPrice(50.0f);
        merchant.setTelephone("123-456-7890");
        merchant.setBusinessHours("9:00-22:00");
        merchant.setDescription("Test Description");
        merchant.setCoverUrl("http://test.com/cover.jpg");
        merchant.setPhotoUrls(Arrays.asList("http://test.com/photo1.jpg", "http://test.com/photo2.jpg"));
        savedMerchant = merchantRepository.save(merchant);
    }

    @Test
    @DisplayName("Should return merchant for existing ID")
    void getMerchantById_ExistingId_ReturnsMerchant() {
        ResponseEntity<Merchant> response = restTemplate.getForEntity(
                "/api/businesses/" + savedMerchant.getId(), Merchant.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Merchant merchant = response.getBody();
        assertThat(merchant).isNotNull();
        assertThat(merchant.getId()).isEqualTo(savedMerchant.getId());
        assertThat(merchant.getMerchantName()).isEqualTo(savedMerchant.getMerchantName());
    }

    @Test
    @DisplayName("Should return 404 for non-existing ID")
    void getMerchantById_NonExistingId_ReturnsNotFound() {
        ResponseEntity<Void> response = restTemplate.getForEntity(
                "/api/businesses/9999", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 400 for negative ID")
    void getMerchantById_NegativeId_ReturnsBadRequest() {
        ResponseEntity<Void> response = restTemplate.getForEntity(
                "/api/businesses/-1", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should return 400 for invalid ID format")
    void getMerchantById_InvalidFormat_ReturnsBadRequest() {
        ResponseEntity<Void> response = restTemplate.getForEntity(
                "/api/businesses/abc", Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
