package com.company.config;

import org.ehcache.jsr107.EhcacheCachingProvider;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;


public class EhcacheConfig {

    public static CacheManager createCacheManager() {
        CachingProvider provider = Caching.getCachingProvider(EhcacheCachingProvider.class.getName());
        return provider.getCacheManager();
    }
}