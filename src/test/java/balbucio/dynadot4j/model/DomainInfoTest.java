package balbucio.dynadot4j.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DomainInfoTest {

    private final Gson gson = new Gson();

    @Test
    void isLockedShouldReturnTrue() {
        DomainInfo info = gson.fromJson("""
                {"domain_name":"example.com","locked":"Yes","disabled":"no","hold":"no","registrant_unverified":"no","status":"active","privacy":"Full Privacy","renew_option":"auto-renew","expiration_date":1767225600000,"registration_date":1609459200000}
                """, DomainInfo.class);
        assertTrue(info.isLocked());
    }

    @Test
    void isDisabledShouldReturnFalse() {
        DomainInfo info = gson.fromJson("""
                {"disabled":"no"}
                """, DomainInfo.class);
        assertFalse(info.isDisabled());
    }

    @Test
    void isHoldShouldReturnTrue() {
        DomainInfo info = gson.fromJson("""
                {"hold":"Yes"}
                """, DomainInfo.class);
        assertTrue(info.isHold());
    }

    @Test
    void isRegistrantUnverifiedShouldReturnFalse() {
        DomainInfo info = gson.fromJson("""
                {"registrant_unverified":"no"}
                """, DomainInfo.class);
        assertFalse(info.isRegistrantUnverified());
    }

    @Test
    void getStatusShouldReturnActive() {
        DomainInfo info = gson.fromJson("""
                {"status":"active"}
                """, DomainInfo.class);
        assertEquals(DomainStatus.ACTIVE, info.getStatus());
    }

    @Test
    void getStatusShouldReturnNullForUnknown() {
        DomainInfo info = gson.fromJson("""
                {"status":"unknown"}
                """, DomainInfo.class);
        assertNull(info.getStatus());
    }

    @Test
    void getPrivacyShouldReturnFull() {
        DomainInfo info = gson.fromJson("""
                {"privacy":"Full Privacy"}
                """, DomainInfo.class);
        assertEquals(DomainPrivacy.FULL, info.getPrivacy());
    }

    @Test
    void getRenewOptionShouldReturnAuto() {
        DomainInfo info = gson.fromJson("""
                {"renew_option":"auto-renew"}
                """, DomainInfo.class);
        assertEquals(DomainRenewOption.AUTO, info.getRenewOption());
    }

    @Test
    void getExpirationDateShouldReturnDate() {
        DomainInfo info = gson.fromJson("""
                {"expiration_date":1767225600000}
                """, DomainInfo.class);
        Date date = info.getExpirationDate();
        assertNotNull(date);
        assertEquals(1767225600000L, date.getTime());
    }

    @Test
    void getExpirationDateShouldReturnNullWhenNull() {
        DomainInfo info = gson.fromJson("{}", DomainInfo.class);
        assertNull(info.getExpirationDate());
    }

    @Test
    void getRegistrationDateShouldReturnDate() {
        DomainInfo info = gson.fromJson("""
                {"registration_date":1609459200000}
                """, DomainInfo.class);
        Date date = info.getRegistrationDate();
        assertNotNull(date);
        assertEquals(1609459200000L, date.getTime());
    }

    @Test
    void getDomainNameShouldReturnName() {
        DomainInfo info = gson.fromJson("""
                {"domain_name":"example.com"}
                """, DomainInfo.class);
        assertEquals("example.com", info.getDomainName());
    }

    @Test
    void toStringShouldNotThrow() {
        DomainInfo info = gson.fromJson("""
                {"domain_name":"example.com","privacy":"Full Privacy","renew_option":"auto-renew","status":"active","locked":"no","disabled":"no","hold":"no","registrant_unverified":"no"}
                """, DomainInfo.class);
        assertNotNull(info.toString());
    }
}
