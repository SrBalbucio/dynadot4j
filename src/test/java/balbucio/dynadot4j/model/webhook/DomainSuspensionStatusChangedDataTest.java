package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainSuspensionStatusChangedDataTest {

    private final Gson gson = new Gson();

    @Test
    void shouldMapFields() {
        DomainSuspensionStatusChangedData data = gson.fromJson("""
                {"domain":"example.com","suspended":true,"suspension_type":"abuse","reason":"spam","message":"suspended","status_changed_timestamp":1700000000000}
                """, DomainSuspensionStatusChangedData.class);
        assertEquals("example.com", data.getDomain());
        assertTrue(data.isSuspended());
        assertEquals("abuse", data.getSuspensionType());
        assertEquals("spam", data.getReason());
        assertEquals("suspended", data.getMessage());
        assertEquals(1700000000000L, data.getStatusChangedTimestamp());
    }

    @Test
    void shouldHandleOptionalFields() {
        DomainSuspensionStatusChangedData data = gson.fromJson("""
                {"domain":"example.com","suspended":false,"reason":""}
                """, DomainSuspensionStatusChangedData.class);
        assertFalse(data.isSuspended());
        assertNull(data.getSuspensionType());
        assertNull(data.getMessage());
    }
}