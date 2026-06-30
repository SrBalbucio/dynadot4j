package balbucio.dynadot4j.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainInfoResponseTest {

    private final Gson gson = new Gson();

    @Test
    void shouldGetDomainInfo() {
        DomainInfoResponse response = gson.fromJson("""
                {"domain_info":{"domain_name":"example.com","locked":"no"}}
                """, DomainInfoResponse.class);
        DomainInfo info = response.getDomainInfo();
        assertNotNull(info);
        assertEquals("example.com", info.getDomainName());
    }
}
