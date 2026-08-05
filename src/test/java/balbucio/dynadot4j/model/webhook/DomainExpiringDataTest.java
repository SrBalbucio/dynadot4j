package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExpiringDataTest {

    private final Gson gson = new Gson();

    @Test
    void shouldMapFields() {
        DomainExpiringData data = gson.fromJson("""
                {"domains_expired_after_30_days":"a.com","domains_expired_after_10_days":"b.com","domains_expired_after_3_days":"c.com","domains_expired_today":"d.com","domains_redemption":"e.com"}
                """, DomainExpiringData.class);
        assertEquals("a.com", data.getDomainsExpiredAfter30Days());
        assertEquals("b.com", data.getDomainsExpiredAfter10Days());
        assertEquals("c.com", data.getDomainsExpiredAfter3Days());
        assertEquals("d.com", data.getDomainsExpiredToday());
        assertEquals("e.com", data.getDomainsRedemption());
    }
}