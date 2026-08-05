package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.json.JSONObject;

import static org.junit.jupiter.api.Assertions.*;

class WebhookEventTest {

    private final Gson gson = new Gson();

    @Test
    void parseShouldRouteOrderCompletedData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"order_completed","event_id":12345,"timestamp":1700000000000,"data":{"order_id":42,"submitted_date":1700000000000,"currency":"USD","total_cost":"100.00","total_paid":"100.00","payment_method":"credit_card","status":"completed","order_item_list":[{"item":"domain","price":"10.00"}]}}
                """, gson);

        assertEquals("order_completed", event.getEvent());
        assertEquals(12345L, event.getEventId());
        assertEquals(1700000000000L, event.getTimestamp());
        assertEquals(WebhookEventType.ORDER_COMPLETED, event.getEventType());

        OrderEventData data = event.getDataAs();
        assertNotNull(data);
        assertEquals(42, data.getOrderId());
        assertEquals(1700000000000L, data.getSubmittedDate());
        assertEquals("USD", data.getCurrency());
        assertEquals("100.00", data.getTotalCost());
        assertEquals("100.00", data.getTotalPaid());
        assertEquals("credit_card", data.getPaymentMethod());
        assertEquals("completed", data.getStatus());
        assertNotNull(data.getOrderItemList());
        assertEquals(1, data.getOrderItemList().size());
        assertEquals("domain", data.getOrderItemList().get(0).getString("item"));
    }

    @Test
    void parseShouldRouteOrderPaymentRequiredData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"order_payment_required","event_id":1,"timestamp":1700000000000,"data":{"order_id":7,"status":"awaiting_payment"}}
                """, gson);
        assertEquals(WebhookEventType.ORDER_PAYMENT_REQUIRED, event.getEventType());
        assertTrue(event.getData() instanceof OrderEventData);
        assertEquals(7, ((OrderEventData) event.getData()).getOrderId());
    }

    @Test
    void parseShouldRouteDomainTransferAwayData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"domain_transfer_away","event_id":2,"timestamp":1700000000000,"data":{"domain":"example.com","gaining_registrar":"Another Registrar","order_id":"ord-1"}}
                """, gson);
        assertEquals(WebhookEventType.DOMAIN_TRANSFER_AWAY, event.getEventType());
        DomainTransferAwayData data = event.getDataAs();
        assertEquals("example.com", data.getDomain());
        assertEquals("Another Registrar", data.getGainingRegistrar());
        assertEquals("ord-1", data.getOrderId());
    }

    @Test
    void parseShouldRouteDomainExpiringData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"domain_expiring","event_id":3,"timestamp":1700000000000,"data":{"domains_expired_after_30_days":"a.com","domains_expired_after_10_days":"b.com","domains_expired_after_3_days":"c.com","domains_expired_today":"d.com","domains_redemption":"e.com"}}
                """, gson);
        assertEquals(WebhookEventType.DOMAIN_EXPIRING, event.getEventType());
        DomainExpiringData data = event.getDataAs();
        assertEquals("a.com", data.getDomainsExpiredAfter30Days());
        assertEquals("b.com", data.getDomainsExpiredAfter10Days());
        assertEquals("c.com", data.getDomainsExpiredAfter3Days());
        assertEquals("d.com", data.getDomainsExpiredToday());
        assertEquals("e.com", data.getDomainsRedemption());
    }

    @Test
    void parseShouldRouteAccountBalanceReminderData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"account_balance_reminder","event_id":4,"timestamp":1700000000000,"data":{"balance_list":[{"currency":"USD","balance":"50.00"}]}}
                """, gson);
        assertEquals(WebhookEventType.ACCOUNT_BALANCE_REMINDER, event.getEventType());
        AccountBalanceReminderData data = event.getDataAs();
        assertNotNull(data.getBalanceList());
        assertEquals(1, data.getBalanceList().size());
        assertEquals("50.00", data.getBalanceList().get(0).getString("balance"));
    }

    @Test
    void parseShouldRouteWhoisVerificationRequiredData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"whois_verification_required","event_id":5,"timestamp":1700000000000,"data":{"whois_name":"John","contact_id":9,"verify_link":"https://example.com","verify_end_time":"2024-01-01","domain_list":[{"domain":"example.com"}]}}
                """, gson);
        assertEquals(WebhookEventType.WHOIS_VERIFICATION_REQUIRED, event.getEventType());
        WhoisVerificationRequiredData data = event.getDataAs();
        assertEquals("John", data.getWhoisName());
        assertEquals(9, data.getContactId());
        assertEquals("https://example.com", data.getVerifyLink());
        assertEquals("2024-01-01", data.getVerifyEndTime());
        assertEquals(1, data.getDomainList().size());
    }

    @Test
    void parseShouldRouteWhoisVerificationNotificationData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"whois_verification_notification","event_id":6,"timestamp":1700000000000,"data":{"contact_id":10,"verification_message":"verify","domain_list":[{"domain":"example.com"}]}}
                """, gson);
        assertEquals(WebhookEventType.WHOIS_VERIFICATION_NOTIFICATION, event.getEventType());
        WhoisVerificationNotificationData data = event.getDataAs();
        assertEquals(10, data.getContactId());
        assertEquals("verify", data.getVerificationMessage());
        assertEquals(1, data.getDomainList().size());
    }

    @Test
    void parseShouldRouteDomainStatusChangedData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"domain_status_changed","event_id":7,"timestamp":1700000000000,"data":{"domain":"example.com","change_type":"expired","expiration":1700000000000,"status":"expired"}}
                """, gson);
        assertEquals(WebhookEventType.DOMAIN_STATUS_CHANGED, event.getEventType());
        DomainStatusChangedData data = event.getDataAs();
        assertEquals("example.com", data.getDomain());
        assertEquals("expired", data.getChangeType());
        assertEquals(1700000000000L, data.getExpiration());
        assertEquals("expired", data.getStatus());
    }

    @Test
    void parseShouldRouteDomainSuspensionStatusChangedData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"domain_suspension_status_changed","event_id":8,"timestamp":1700000000000,"data":{"domain":"example.com","suspended":true,"suspension_type":"abuse","reason":"spam","message":"suspended","status_changed_timestamp":1700000000000}}
                """, gson);
        assertEquals(WebhookEventType.DOMAIN_SUSPENSION_STATUS_CHANGED, event.getEventType());
        DomainSuspensionStatusChangedData data = event.getDataAs();
        assertEquals("example.com", data.getDomain());
        assertTrue(data.isSuspended());
        assertEquals("abuse", data.getSuspensionType());
        assertEquals("spam", data.getReason());
        assertEquals("suspended", data.getMessage());
        assertEquals(1700000000000L, data.getStatusChangedTimestamp());
    }

    @Test
    void parseShouldRouteMaintenanceNoticeData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"maintenance_notice","event_id":9,"timestamp":1700000000000,"data":{"start_time":1700000000000,"end_time":1700003600000,"available_services":"lookup","unavailable_services":"register","registry_name":"Verisign","affected_tlds":".com"}}
                """, gson);
        assertEquals(WebhookEventType.MAINTENANCE_NOTICE, event.getEventType());
        MaintenanceNoticeData data = event.getDataAs();
        assertEquals(1700000000000L, data.getStartTime());
        assertEquals(1700003600000L, data.getEndTime());
        assertEquals("lookup", data.getAvailableServices());
        assertEquals("register", data.getUnavailableServices());
        assertEquals("Verisign", data.getRegistryName());
        assertEquals(".com", data.getAffectedTlds());
    }

    @Test
    void parseShouldKeepRawDataForUnknownEvent() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"some_future_event","event_id":9,"timestamp":1700000000000,"data":{"key":"value"}}
                """, gson);
        assertNull(event.getEventType());
        assertTrue(event.getData() instanceof JSONObject);
        assertEquals("value", ((JSONObject) event.getData()).getString("key"));
    }

    @Test
    void parseShouldHandleNullData() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"domain_status_changed","event_id":7,"timestamp":1700000000000,"data":null}
                """, gson);
        assertNull(event.getData());
        assertEquals(WebhookEventType.DOMAIN_STATUS_CHANGED, event.getEventType());
    }

    @Test
    void fromLabelShouldMapAllTypes() {
        assertEquals(WebhookEventType.ORDER_COMPLETED, WebhookEventType.fromLabel("order_completed"));
        assertEquals(WebhookEventType.MAINTENANCE_NOTICE, WebhookEventType.fromLabel("maintenance_notice"));
        assertNull(WebhookEventType.fromLabel("unknown"));
    }

    @Test
    void toStringShouldNotThrow() {
        WebhookEvent<?> event = WebhookEvent.parse("""
                {"event":"order_completed","event_id":12345,"timestamp":1700000000000,"data":{"order_id":42}}
                """, gson);
        assertNotNull(event.toString());
    }
}