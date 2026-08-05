package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WhoisVerificationNotificationDataTest {

    private final Gson gson = new Gson();

    @Test
    void shouldMapFields() {
        WhoisVerificationNotificationData data = gson.fromJson("""
                {"contact_id":10,"verification_message":"verify","domain_list":[{"domain":"example.com"}]}
                """, WhoisVerificationNotificationData.class);
        assertEquals(10, data.getContactId());
        assertEquals("verify", data.getVerificationMessage());
        assertEquals(1, data.getDomainList().size());
    }
}