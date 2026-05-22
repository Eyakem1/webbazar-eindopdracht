package com.webbazar.repo;

import com.webbazar.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);


    List<Order> findAllByUserEmailOrderByIdDesc(String email);

    // Admin-list
    List<Order> findAllByOrderByIdDesc();
}
