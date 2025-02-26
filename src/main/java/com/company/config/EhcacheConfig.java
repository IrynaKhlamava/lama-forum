package com.company.config;

import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;


@Configuration
public class EhcacheConfig {

    public static CacheManager createCacheManager() {
        CachingProvider provider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        return provider.getCacheManager();
    }

    @Bean
    public CacheManager cacheManager() {
        return EhcacheConfig.createCacheManager();
    }
}