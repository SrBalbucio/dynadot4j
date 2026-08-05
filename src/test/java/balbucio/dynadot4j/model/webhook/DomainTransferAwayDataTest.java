package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainTransferAwayDataTest {

    private final Gson gson = new Gson();

    @Test
    void shouldMapFields() {
        DomainTransferAwayData data = gson.fromJson("""
                {"domain":"example.com","gaining_registrar":"Another Registrar","order_id":"ord-1"}
                """, DomainTransferAwayData.class);
        assertEquals("example.com", data.getDomain());
        assertEquals("Another Registrar", data.getGainingRegistrar());
        assertEquals("ord-1", data.getOrderId());
    }

    @Test
    void toStringShouldNotThrow() {
        DomainTransferAwayData data = gson.fromJson("""
                {"domain":"example.com"}
                """, DomainTransferAwayData.class);
        assertNotNull(data.toString());
    }
}