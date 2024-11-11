package com.nuamx.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CleanCacheService {

    private CacheManager cacheManager;

    public void execute(String jobName) {
        Cache cache = cacheManager.getCache("metadata");
        if (cache != null) {
            cache.clear();
            log.info("[{}] - Cleaning cache process", jobName);
        } else {
            log.info("[{}] - Nothing to clean", jobName);
        }
    }

}
