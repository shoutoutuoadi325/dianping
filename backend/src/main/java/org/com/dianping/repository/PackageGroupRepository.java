package org.com.dianping.repository;

import org.com.dianping.entity.PackageGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PackageGroupRepository extends JpaRepository<PackageGroup, Long> {
    // 根据商户ID查询套餐列表
    List<PackageGroup> findByMerchantId(Long merchantId);

    @Modifying
    @Query("UPDATE PackageGroup p SET p.sales = p.sales + 1 WHERE p.id = :packageId")
    void incrementSales(@Param("packageId") long packageId);
}