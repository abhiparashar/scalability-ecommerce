package com.scalability_ecommerce.scalability_ecommerce.repository;

import com.scalability_ecommerce.scalability_ecommerce.model.Product;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.awt.print.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Optimized query with caching hint
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.stockQuantity > 0")
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL")
    })
    Page<Product> findByCategoryWithStock(@Param("category") String category, Pageable pageable);
}
