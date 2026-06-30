package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BulkSearchResultTest {

    @Test
    void isAvailableShouldReturnTrueForYes() {
        BulkSearchResult result = new BulkSearchResult();
        result.setAvailable("Yes");
        assertTrue(result.isAvailable());
    }

    @Test
    void isAvailableShouldReturnFalseForNo() {
        BulkSearchResult result = new BulkSearchResult();
        result.setAvailable("no");
        assertFalse(result.isAvailable());
    }

    @Test
    void isAvailableShouldReturnFalseForNull() {
        BulkSearchResult result = new BulkSearchResult();
        result.setAvailable(null);
        assertFalse(result.isAvailable());
    }

    @Test
    void shouldHandleAllArgsConstructor() {
        BulkSearchResult result = new BulkSearchResult("example.com", "Yes");
        assertEquals("example.com", result.getDomainName());
        assertTrue(result.isAvailable());
    }
}
