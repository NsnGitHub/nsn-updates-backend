
/**
 * Initially thought caching AppUser would be a good idea since it is queried a lot.
 *
 * However, due to the frequent changes of state such as posts, followers, following, notifications, etc. it would require
 * a lot of cache invalidation and would be better to just query again for it.
 */

//package com.nsn.nsnupdatesbackend.config;
//
//import com.nsn.nsnupdatesbackend.user.AppUser;
//import org.ehcache.config.builders.CacheConfigurationBuilder;
//import org.ehcache.config.builders.ExpiryPolicyBuilder;
//import org.ehcache.config.builders.ResourcePoolsBuilder;
//import org.ehcache.config.units.MemoryUnit;
//import org.ehcache.jsr107.Eh107Configuration;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.jcache.JCacheCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.cache.CacheManager;
//import javax.cache.Caching;
//import javax.cache.spi.CachingProvider;
//import java.time.Duration;
//
//@Configuration
//@EnableCaching
//public class CacheConfig {
//
//    @Bean
//    public JCacheCacheManager jCacheCacheManager(CacheManager cacheManager) {
//        return new JCacheCacheManager(cacheManager);
//    }
//
//    @Primary
//    @Bean(value = "cacheManager")
//    public CacheManager cacheManager() {
//        CachingProvider cachingProvider = Caching.getCachingProvider();
//        CacheManager cacheManager = cachingProvider.getCacheManager();
//
//        CacheConfigurationBuilder<String, AppUser> config = CacheConfigurationBuilder
//                .newCacheConfigurationBuilder(
//                        String.class,
//                        AppUser.class,
//                        ResourcePoolsBuilder
//                                .newResourcePoolsBuilder().offheap(1, MemoryUnit.MB)
//                ).withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(10)));
//
//        javax.cache.configuration.Configuration<String, AppUser> userCacheConfiguration = Eh107Configuration
//                .fromEhcacheCacheConfiguration(config);
//
//        if (cacheManager.getCache("users") == null) {
//            System.out.println("--- CACHE LOGGING --- Creating cache for 'users'");
//            cacheManager.createCache("users", userCacheConfiguration);
//        }
//
//        return cacheManager;
//    }
//}
