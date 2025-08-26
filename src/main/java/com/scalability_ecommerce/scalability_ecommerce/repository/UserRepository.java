package com.scalability_ecommerce.scalability_ecommerce.repository;

import com.scalability_ecommerce.scalability_ecommerce.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Cached user lookup by username
    @Query("SELECT u FROM User u WHERE u.username = :username")
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "userCache")
    })
    Optional<User>findByUsername(@Param("username") String username);

    // Cached user lookup by email
    @Query("SELECT p FROM User WHERE p.email = :email")
    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true")
    })
    Optional<User>findByEmail(@Param("email") String email);

    // Update last login time efficiently
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :loginTime WHERE u.id = :userId")
    int updateLastLogin(@Param("userId") Long userId, @Param("loginTime") LocalDateTime loginTime);

    // Find active users for session management
    @Query("SELECT u FROM User u WHERE u.lastLogin > :since")
    List<User> findActiveUsers(@Param("since") LocalDateTime since);

    // User statistics for monitoring
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt > :since")
    long countNewUsers(@Param("since") LocalDateTime since);

    // Check if username exists (for validation)
    boolean existsByUsername(String username);

    // Check if email exists (for validation)
    boolean existsByEmail(String email);
}
