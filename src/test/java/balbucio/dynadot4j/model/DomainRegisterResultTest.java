package balbucio.dynadot4j.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

class DomainRegisterResultTest {

    private final Gson gson = new Gson();

    @Test
    void getExpirationDateShouldReturnDate() {
        DomainRegisterResult result = gson.fromJson("""
                {"domain_name":"example.com","expiration_date":1767225600000}
                """, DomainRegisterResult.class);
        Date date = result.getExpirationDate();
        assertNotNull(date);
        assertEquals(1767225600000L, date.getTime());
    }

    @Test
    void getExpirationDateTimeShouldConvertWithTimeZone() {
        DomainRegisterResult result = gson.fromJson("""
                {"domain_name":"example.com","expiration_date":1767225600000}
                """, DomainRegisterResult.class);
        LocalDateTime dateTime = result.getExpirationDateTime(TimeZone.getTimeZone("UTC"));
        assertNotNull(dateTime);
    }

    @Test
    void getExpirationDateTimeShouldUseProvidedTimeZone() {
        DomainRegisterResult result = gson.fromJson("""
                {"domain_name":"example.com","expiration_date":1767225600000}
                """, DomainRegisterResult.class);
        LocalDateTime utc = result.getExpirationDateTime(TimeZone.getTimeZone("UTC"));
        LocalDateTime brt = result.getExpirationDateTime(TimeZone.getTimeZone("America/Sao_Paulo"));
        assertNotEquals(utc, brt);
    }

    @Test
    void toStringShouldNotThrow() {
        DomainRegisterResult result = gson.fromJson("""
                {"domain_name":"example.com","expiration_date":0}
                """, DomainRegisterResult.class);
        assertNotNull(result.toString());
    }
}
