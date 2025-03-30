package org.com.dianping.repository;

import org.com.dianping.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MerchantRepository extends JpaRepository<Merchant, Long>, JpaSpecificationExecutor<Merchant> {
    @Query("SELECT m FROM Merchant m WHERE LOWER(m.merchantName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Merchant> fuzzySearch(String keyword);
}