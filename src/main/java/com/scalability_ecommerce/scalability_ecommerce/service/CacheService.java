package com.scalability_ecommerce.scalability_ecommerce.service;

import com.scalability_ecommerce.scalability_ecommerce.model.Product;
import com.scalability_ecommerce.scalability_ecommerce.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String PRODUCT_CACHE_PREFIX = "product:";
    private static final String USER_CACHE_PREFIX = "user:";
    private static final String CATEGORY_CACHE_PREFIX = "category:";

    // Multi-level caching for user profiles
    @Cacheable(value = "userProfiles", key = "#username", cacheManager = "localCacheManager")
    public User getUserProfileFromLocalCache(String username) {
        log.debug("Loading user profile for {} from local cache", username);
        return getUserProfileFromRedis(username);
    }

    @Cacheable(value = "userProfiles", key = "#username")
    public User getUserProfileFromRedis(String username) {
        log.debug("Loading user profile for {} from Redis", username);
        // This would typically load from database
        return null; // Implementation in UserService
    }

    // Product caching with cache warming
    @Cacheable(value = "productCatalog", key = "#categoryId", cacheManager = "localCacheManager")
    public List<Product> getProductsByCategory(Long categoryId) {
        log.debug("Loading products for category {} from local cache", categoryId);
        return getProductsByCategoryFromRedis(categoryId);
    }

    @Cacheable(value = "productCatalog", key = "#categoryId")
    public List<Product> getProductsByCategoryFromRedis(Long categoryId) {
        log.debug("Loading products for category {} from Redis", categoryId);
        // Implementation in ProductService
        return null;
    }

    // Cache invalidation strategies
    @CacheEvict(value = "productCatalog", key = "#categoryId", allEntries = false)
    public void evictProductCategory(Long categoryId) {
        log.info("Evicted product category cache for category: {}", categoryId);
    }

    @CacheEvict(value = "userProfiles", key = "#username")
    public void evictUserProfile(String username) {
        log.info("Evicted user profile cache for user: {}", username);
    }

    // Cache warming strategies
    public CompletableFuture<Void> warmProductCache(List<String> popularCategories) {
        return CompletableFuture.runAsync(() -> {
            log.info("Starting cache warming for {} categories", popularCategories.size());
            popularCategories.forEach(category -> {
                try {
                    // Pre-load popular categories into cache
                    String cacheKey = CATEGORY_CACHE_PREFIX + category;
                    redisTemplate.opsForValue().set(cacheKey, "warmed", Duration.ofHours(1));
                    log.debug("Warmed cache for category: {}", category);
                } catch (Exception e) {
                    log.error("Failed to warm cache for category: {}", category, e);
                }
            });
            log.info("Cache warming completed");
        });
    }

    // Cache statistics for monitoring
    public void logCacheStatistics() {
        try {
            Set<String> keys = redisTemplate.keys("*");
            log.info("Redis cache contains {} keys", keys != null ? keys.size() : 0);
        } catch (Exception e) {
            log.error("Failed to get cache statistics", e);
        }
    }

    // Manual cache operations
    public void setWithExpiration(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
