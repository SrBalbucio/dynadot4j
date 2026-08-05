package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderEventDataTest {

    private final Gson gson = new Gson();

    @Test
    void shouldMapFields() {
        OrderEventData data = gson.fromJson("""
                {"order_id":42,"submitted_date":1700000000000,"currency":"USD","total_cost":"100.00","total_paid":"100.00","payment_method":"credit_card","status":"completed","order_item_list":[{"item":"domain"}]}
                """, OrderEventData.class);
        assertEquals(42, data.getOrderId());
        assertEquals(1700000000000L, data.getSubmittedDate());
        assertEquals("USD", data.getCurrency());
        assertEquals("100.00", data.getTotalCost());
        assertEquals("100.00", data.getTotalPaid());
        assertEquals("credit_card", data.getPaymentMethod());
        assertEquals("completed", data.getStatus());
        assertEquals(1, data.getOrderItemList().size());
    }

    @Test
    void toStringShouldNotThrow() {
        OrderEventData data = gson.fromJson("""
                {"order_id":42,"status":"completed"}
                """, OrderEventData.class);
        assertNotNull(data.toString());
    }
}