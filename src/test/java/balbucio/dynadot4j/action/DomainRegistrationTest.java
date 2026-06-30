package balbucio.dynadot4j.action;

import balbucio.dynadot4j.model.DomainPrivacy;
import balbucio.dynadot4j.model.RegistrantContact;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DomainRegistrationTest {

    @Test
    void createShouldReturnRegistration() {
        DomainRegistration reg = DomainRegistration.create("example.com");
        assertEquals("example.com", reg.getDomainName());
        assertEquals(1, reg.getDuration());
    }

    @Test
    void withDurationShouldSetDuration() {
        DomainRegistration reg = DomainRegistration.create("test.com").withDuration(5);
        assertEquals(5, reg.getDuration());
    }

    @Test
    void withAuthCodeShouldSetAuthCode() {
        DomainRegistration reg = DomainRegistration.create("test.com").withAuthCode("abc123");
        assertEquals("abc123", reg.getAuthCode());
    }

    @Test
    void withCustomerIdShouldSetCustomerId() {
        DomainRegistration reg = DomainRegistration.create("test.com").withCustomerId(42);
        assertEquals(42, reg.getCustomerId());
    }

    @Test
    void addNSShouldAddNameserver() {
        DomainRegistration reg = DomainRegistration.create("test.com")
                .addNS("ns1.example.com")
                .addNS("ns2.example.com");
        assertEquals(2, reg.getNameserver().size());
        assertTrue(reg.getNameserver().contains("ns1.example.com"));
    }

    @Test
    void withNSShouldReplaceNameservers() {
        DomainRegistration reg = DomainRegistration.create("test.com")
                .addNS("ns1.example.com")
                .withNS(List.of("ns3.example.com"));
        assertEquals(1, reg.getNameserver().size());
        assertEquals("ns3.example.com", reg.getNameserver().get(0));
    }

    @Test
    void withPrivacyShouldSetPrivacy() {
        DomainRegistration reg = DomainRegistration.create("test.com").withPrivacy(DomainPrivacy.PARTIAL);
        assertEquals(DomainPrivacy.PARTIAL, reg.getPrivacy());
    }

    @Test
    void withCurrencyShouldSetCurrency() {
        DomainRegistration reg = DomainRegistration.create("test.com").withCurrency("BRL");
        assertEquals("BRL", reg.getCurrency());
    }

    @Test
    void setPremiumShouldEnablePremium() {
        DomainRegistration reg = DomainRegistration.create("test.com").setPremium(true);
        assertTrue(reg.isRegisterPremium());
    }

    @Test
    void withCouponCodeShouldSetCoupon() {
        DomainRegistration reg = DomainRegistration.create("test.com").withCouponCode("SAVE10");
        assertEquals("SAVE10", reg.getCouponCode());
    }

    @Test
    void withContactShouldSetAllContacts() {
        RegistrantContact contact = RegistrantContact.builder()
                .name("John")
                .email("john@test.com")
                .build();

        DomainRegistration reg = DomainRegistration.create("test.com").withContact(contact);

        assertSame(contact, reg.getRegistrant());
        assertSame(contact, reg.getAdmin());
        assertSame(contact, reg.getTech());
        assertSame(contact, reg.getBilling());
    }

    @Test
    void withRegistrantContactShouldSetOnlyRegistrant() {
        RegistrantContact contact = RegistrantContact.builder().name("John").build();
        DomainRegistration reg = DomainRegistration.create("test.com").withRegistrantContact(contact);
        assertSame(contact, reg.getRegistrant());
        assertNull(reg.getAdmin());
    }

    @Test
    void toJSONShouldContainAllFields() {
        RegistrantContact contact = RegistrantContact.builder()
                .name("John").email("j@j.com").phoneNumber("123").phoneCC("55")
                .address("Addr").city("City").state("ST").country("BR").organization("Org")
                .build();

        DomainRegistration reg = DomainRegistration.create("test.com")
                .withDuration(2)
                .withAuthCode("auth")
                .withCustomerId(1)
                .addNS("ns1.example.com")
                .withPrivacy(DomainPrivacy.FULL)
                .withCurrency("USD")
                .setPremium(false)
                .withCouponCode("CODE")
                .withContact(contact);

        JSONObject json = reg.toJSON();

        assertEquals("USD", json.getString("currency"));
        assertEquals("CODE", json.getString("coupon_code"));
        assertFalse(json.getBoolean("register_premium"));

        JSONObject domain = json.getJSONObject("domain");
        assertEquals(2, domain.getInt("duration"));
        assertEquals("auth", domain.getString("auth_code"));
        assertEquals(1, domain.getInt("customer_id"));
        assertEquals("full", domain.getString("privacy"));
        assertTrue(domain.getJSONArray("name_server_list").length() > 0);
    }

    @Test
    void toStringShouldNotThrow() {
        DomainRegistration reg = DomainRegistration.create("test.com");
        assertNotNull(reg.toString());
    }
}
