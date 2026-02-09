package simvex.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("presignedUrls");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(8, TimeUnit.MINUTES) // 작성 후 8분 뒤 만료
                .maximumSize(100));

        return cacheManager;
    }
}
