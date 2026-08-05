package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainStatusChangedDataTest {

    private final Gson gson = new Gson();

    @Test
    void shouldMapFields() {
        DomainStatusChangedData data = gson.fromJson("""
                {"domain":"example.com","change_type":"expired","expiration":1700000000000,"status":"expired"}
                """, DomainStatusChangedData.class);
        assertEquals("example.com", data.getDomain());
        assertEquals("expired", data.getChangeType());
        assertEquals(1700000000000L, data.getExpiration());
        assertEquals("expired", data.getStatus());
    }
}