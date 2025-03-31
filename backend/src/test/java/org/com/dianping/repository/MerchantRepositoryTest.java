package org.com.dianping.repository;

import org.com.dianping.entity.Merchant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MerchantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MerchantRepository merchantRepository;

    private Merchant merchant1;
    private Merchant merchant2;

    @BeforeEach
    void setUp() {
        // 创建测试商家数据
        merchant1 = new Merchant(
                0L, "Restaurant A", "Restaurant", 4.5, "Location A",
                80.0, 12345678L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Chinese, Spicy", "url1");

        merchant2 = new Merchant(
                0L, "Coffee Shop B", "Cafe", 4.2, "Location B",
                40.0, 87654321L, new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()), "Coffee, Dessert", "url2");

        // 保存到测试数据库
        entityManager.persist(merchant1);
        entityManager.persist(merchant2);
        entityManager.flush();
    }

    @Test
    void findById_ShouldReturnMerchant_WhenMerchantExists() {
        // when
        Optional<Merchant> foundMerchant = merchantRepository.findById(merchant1.getId());

        // then
        assertTrue(foundMerchant.isPresent());
        assertEquals("Restaurant A", foundMerchant.get().getMerchantName());
        assertEquals("Restaurant", foundMerchant.get().getType());
    }

    @Test
    void findById_ShouldReturnEmptyOptional_WhenMerchantDoesNotExist() {
        // when
        Optional<Merchant> foundMerchant = merchantRepository.findById(999L);

        // then
        assertFalse(foundMerchant.isPresent());
    }

    @Test
    void fuzzySearch_ShouldReturnMatchingMerchants() {
        // when
        List<Merchant> result = merchantRepository.fuzzySearch("restaurant");

        // then
        assertEquals(1, result.size());
        assertEquals("Restaurant A", result.get(0).getMerchantName());
    }

    @Test
    void fuzzySearch_ShouldBeCaseInsensitive() {
        // when - 使用小写搜索
        List<Merchant> result1 = merchantRepository.fuzzySearch("restaurant");
        // when - 使用大写搜索
        List<Merchant> result2 = merchantRepository.fuzzySearch("RESTAURANT");

        // then
        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertEquals("Restaurant A", result1.get(0).getMerchantName());
        assertEquals("Restaurant A", result2.get(0).getMerchantName());
    }

    @Test
    void fuzzySearch_ShouldReturnEmptyList_WhenNoMatchFound() {
        // when
        List<Merchant> result = merchantRepository.fuzzySearch("nonexistent");

        // then
        assertTrue(result.isEmpty());
    }
}
