package balbucio.dynadot4j.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DomainPriceEntryTest {

    private final Gson gson = new Gson();

    private DomainPriceEntry createEntry(String json) {
        return gson.fromJson(json, DomainPriceEntry.class);
    }

    @Test
    void getPeriodShouldExtractYear() {
        DomainPriceEntry entry = createEntry("""
                {"currency":"USD","unit":"0/1 year","registration_price":"$10.00","renewal_price":"$12.00"}
                """);
        assertEquals(1, entry.getPeriod());
    }

    @Test
    void registrationPriceAsDoubleShouldParseUsd() {
        DomainPriceEntry entry = createEntry("""
                {"currency":"USD","unit":"(1 year)","registration_price":"$10.50"}
                """);
        assertEquals(10.50, entry.registrationPriceAsDouble(), 0.001);
    }

    @Test
    void registrationPriceAsDoubleShouldReturnZeroForProblemPrices() {
        DomainPriceEntry entry = createEntry("""
                {"currency":"USD","registration_price":"Problem getting prices"}
                """);
        assertEquals(0.0, entry.registrationPriceAsDouble(), 0.001);
    }

    @Test
    void registrationPriceAsDecimalShouldParse() {
        DomainPriceEntry entry = createEntry("""
                {"currency":"USD","registration_price":"$10.50"}
                """);
        assertEquals(new BigDecimal("10.50"), entry.registrationPriceAsDecimal());
    }

    @Test
    void renewalPriceAsDoubleShouldUseRegistrationPrice() {
        DomainPriceEntry entry = createEntry("""
                {"currency":"USD","registration_price":"$20.00"}
                """);
        assertEquals(20.00, entry.renewalPriceAsDouble(), 0.001);
    }

    @Test
    void transferPriceAsDoubleShouldUseRegistrationPrice() {
        DomainPriceEntry entry = createEntry("""
                {"currency":"USD","registration_price":"$15.00"}
                """);
        assertEquals(15.00, entry.transferPriceAsDouble(), 0.001);
    }

    @Test
    void restorePriceAsDoubleShouldUseRegistrationPrice() {
        DomainPriceEntry entry = createEntry("""
                {"currency":"USD","registration_price":"$25.00"}
                """);
        assertEquals(25.00, entry.restorePriceAsDouble(), 0.001);
    }

    @Test
    void toStringShouldNotThrow() {
        DomainPriceEntry entry = createEntry("""
                {"currency":"USD","registration_price":"$10.00"}
                """);
        assertNotNull(entry.toString());
    }

    @Test
    void equalsAndHashCodeShouldWork() {
        DomainPriceEntry a = createEntry("""
                {"currency":"USD","unit":"0/1 year","registration_price":"$10.00"}
                """);
        DomainPriceEntry b = createEntry("""
                {"currency":"USD","unit":"0/1 year","registration_price":"$10.00"}
                """);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
