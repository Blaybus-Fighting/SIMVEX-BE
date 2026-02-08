package simvex.global.auth.ticket;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthTicketConfig {

    @Bean
    public Cache<String, String> authTicketCache(
            @Value("${login.ticket.ttl-seconds}") long ttlSeconds,
            @Value("${login.ticket.max-size}") long maxSize
    ) {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(maxSize)
                .build();
    }
}
