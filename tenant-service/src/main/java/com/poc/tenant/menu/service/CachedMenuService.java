package com.poc.tenant.menu.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.tenant.menu.domain.MenuItem;
import com.poc.tenant.menu.dto.MenuItemDTO;
import com.poc.tenant.menu.repository.MenuItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CachedMenuService {

    private static final String CACHE_KEY = "menu:platform:all";

    private final MenuItemRepository menuItemRepository;
    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;

    @Value("${app.cache.menu.ttl:60}")
    private long cacheTtlMinutes;

    public CachedMenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        evictCache();
    }

    public List<MenuItemDTO> getAllMenus() {
        // Try cache first
        if (redisTemplate != null && cacheTtlMinutes > 0) {
            try {
                String cached = redisTemplate.opsForValue().get(CACHE_KEY);
                if (cached != null) {
                    log.debug("Menu cache hit");
                    return objectMapper.readValue(cached, new TypeReference<List<MenuItemDTO>>() {});
                }
            } catch (Exception e) {
                log.warn("Redis cache read failed, falling back to DB: {}", e.getMessage());
            }
        }

        // Cache miss or Redis unavailable - fetch from local DB
        log.debug("Menu cache miss, fetching from local DB");
        List<MenuItem> entities = menuItemRepository.findAllOrderByOrderIndex();
        List<MenuItemDTO> menus = entities.stream()
                .map(MenuItemDTO::from)
                .collect(Collectors.toList());

        // Store in cache
        if (redisTemplate != null && cacheTtlMinutes > 0) {
            try {
                String json = objectMapper.writeValueAsString(menus);
                redisTemplate.opsForValue().set(CACHE_KEY, json, cacheTtlMinutes, TimeUnit.MINUTES);
                log.debug("Menu cache stored, {} items, TTL {} min", menus.size(), cacheTtlMinutes);
            } catch (Exception e) {
                log.warn("Redis cache write failed: {}", e.getMessage());
            }
        }

        return menus;
    }

    public void evictCache() {
        if (redisTemplate != null) {
            try {
                redisTemplate.delete(CACHE_KEY);
                log.info("Menu cache evicted");
            } catch (Exception e) {
                log.warn("Redis cache eviction failed: {}", e.getMessage());
            }
        }
    }
}
