package com.scalability_ecommerce.scalability_ecommerce.repository;

import com.scalability_ecommerce.scalability_ecommerce.model.Order;
import com.scalability_ecommerce.scalability_ecommerce.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.QueryHint;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // User's order history with pagination
    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.orderDate DESC")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Page<Order> findByUser(@Param("user") User user, Pageable pageable);

    // Orders by status for processing
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.orderDate ASC")
    List<Order> findByStatus(@Param("status") Order.OrderStatus status);

    // Recent orders for monitoring
    @Query("SELECT o FROM Order o WHERE o.orderDate > :since ORDER BY o.orderDate DESC")
    List<Order> findRecentOrders(@Param("since") LocalDateTime since);

    // Revenue calculations
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.status != 'CANCELLED'")
    BigDecimal calculateRevenue(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    // Order statistics by date range
    @Query("SELECT COUNT(o), AVG(o.totalAmount), MAX(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Object[] getOrderStatistics(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);

    // Top customers by order value
    @Query("SELECT o.user, SUM(o.totalAmount) as totalSpent FROM Order o " +
            "WHERE o.status != 'CANCELLED' GROUP BY o.user ORDER BY totalSpent DESC")
    List<Object[]> findTopCustomers(Pageable pageable);

    // Orders requiring processing (performance critical)
    @Query("SELECT o FROM Order o WHERE o.status IN ('PENDING', 'CONFIRMED') " +
            "AND o.orderDate < :cutoffTime ORDER BY o.orderDate ASC")
    List<Order> findOrdersRequiringProcessing(@Param("cutoffTime") LocalDateTime cutoffTime);

    // Count orders by user for rate limiting
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user = :user AND o.orderDate > :since")
    long countUserOrdersSince(@Param("user") User user, @Param("since") LocalDateTime since);
}