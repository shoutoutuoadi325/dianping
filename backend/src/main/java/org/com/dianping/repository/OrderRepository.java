package org.com.dianping.repository;

import org.com.dianping.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);
}