package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceNoticeDataTest {

    private final Gson gson = new Gson();

    @Test
    void shouldMapFields() {
        MaintenanceNoticeData data = gson.fromJson("""
                {"start_time":1700000000000,"end_time":1700003600000,"available_services":"lookup","unavailable_services":"register","registry_name":"Verisign","affected_tlds":".com"}
                """, MaintenanceNoticeData.class);
        assertEquals(1700000000000L, data.getStartTime());
        assertEquals(1700003600000L, data.getEndTime());
        assertEquals("lookup", data.getAvailableServices());
        assertEquals("register", data.getUnavailableServices());
        assertEquals("Verisign", data.getRegistryName());
        assertEquals(".com", data.getAffectedTlds());
    }
}