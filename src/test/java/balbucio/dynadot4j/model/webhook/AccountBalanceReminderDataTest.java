package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountBalanceReminderDataTest {

    private final Gson gson = new Gson();

    @Test
    void shouldMapFields() {
        AccountBalanceReminderData data = gson.fromJson("""
                {"balance_list":[{"currency":"USD","balance":"50.00"}]}
                """, AccountBalanceReminderData.class);
        assertNotNull(data.getBalanceList());
        assertEquals(1, data.getBalanceList().size());
    }
}