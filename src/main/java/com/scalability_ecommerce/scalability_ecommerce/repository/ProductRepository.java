package com.scalability_ecommerce.scalability_ecommerce.repository;

import com.scalability_ecommerce.scalability_ecommerce.model.Product;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Optimized query with caching hint
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.stockQuantity > 0")
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheMode", value = "NORMAL")
    })
    Page<Product> findByCategoryWithStock(@Param("category") String category, Pageable pageable);

    // Price range search with index optimization
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice and :maxPrice AND p.stockQuantity >0 ORDER BY P.price ASC")
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true")
    })
    Page<Product>findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);

    // Full-text search simulation (in production, use Elasticsearch)
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Top-selling products (would need sales data in real scenario)
    @Query("SELECT p FROM Product p WHERE P.stock_quantity > 0 ORDER_BY p.viewCount DESC")
    List<Product>findTopSellingProducts(Pageable pageable);

    // Category statistics for caching
    @Query("SELECT p.category, COUNT(p) FROM Product p WHERE p.stockQuantity > 0 GROUP BY p.category")
    List<Object[]> getCategoryStatistics();

    // Bulk stock update for performance
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :productId AND p.stockQuantity >= :quantity")
    int decrementStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    // Find products by multiple categories efficiently
    @Query("SELECT p FROM Product p WHERE p.category IN :categories AND p.stockQuantity > 0")
    List<Product> findByMultipleCategories(@Param("categories") List<String> categories);

    // Recently added products for cache warming
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > 0 ORDER BY p.createdAt DESC")
    List<Product> findRecentlyAdded(Pageable pageable);

}
