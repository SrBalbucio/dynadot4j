package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WhoisVerificationRequiredDataTest {

    private final Gson gson = new Gson();

    @Test
    void shouldMapFields() {
        WhoisVerificationRequiredData data = gson.fromJson("""
                {"whois_name":"John","contact_id":9,"verify_link":"https://example.com","verify_end_time":"2024-01-01","domain_list":[{"domain":"example.com"}]}
                """, WhoisVerificationRequiredData.class);
        assertEquals("John", data.getWhoisName());
        assertEquals(9, data.getContactId());
        assertEquals("https://example.com", data.getVerifyLink());
        assertEquals("2024-01-01", data.getVerifyEndTime());
        assertEquals(1, data.getDomainList().size());
    }
}