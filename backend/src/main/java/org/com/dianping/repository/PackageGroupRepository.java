package org.com.dianping.repository;

import org.com.dianping.entity.PackageGroup;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PackageGroupRepository extends JpaRepository<PackageGroup, Long>{
    void incrementSales(Long packageId);

}