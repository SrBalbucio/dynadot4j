package balbucio.dynadot4j.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InvalidDomainExceptionTest {

    @Test
    void shouldCreateWithSingleDomain() {
        InvalidDomainException ex = new InvalidDomainException("example.com");
        assertTrue(ex.getMessage().contains("example.com"));
        assertTrue(ex.getMessage().contains("invalid"));
    }

    @Test
    void shouldCreateWithDomainList() {
        List<String> domains = List.of("example.com", "test.org");
        InvalidDomainException ex = new InvalidDomainException(domains);
        assertTrue(ex.getMessage().contains("example.com"));
        assertTrue(ex.getMessage().contains("test.org"));
    }
}
