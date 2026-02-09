package simvex.global.auth.ticket;

import com.github.benmanes.caffeine.cache.Cache;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class AuthTicketService {
    private static final int TICKET_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();

    private final Cache<String, String> ticketCache;

    public AuthTicketService(Cache<String, String> ticketCache) {
        this.ticketCache = ticketCache;
    }

    public String issue(String token) {
        String ticket = generateTicket();
        ticketCache.put(ticket, token);
        return ticket;
    }

    public Optional<String> consume(String ticket) {
        if (ticket == null || ticket.isBlank()) {
            return Optional.empty();
        }

        String token = ticketCache.getIfPresent(ticket);
        if (token == null) {
            return Optional.empty();
        }

        ticketCache.invalidate(ticket);
        return Optional.of(token);
    }

    private String generateTicket() {
        String ticket;
        do {
            byte[] bytes = new byte[TICKET_BYTES];
            SECURE_RANDOM.nextBytes(bytes);
            ticket = ENCODER.encodeToString(bytes);
        } while (ticketCache.getIfPresent(ticket) != null);

        return ticket;
    }
}
