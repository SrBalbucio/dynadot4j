package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListingItemTest {

    @Test
    void shouldParseStringBooleanVariants() {
        ListingItem item = new ListingItem();
        item.setIsPendingSaleLocked("Yes");
        assertTrue(item.isPendingSaleLocked());

        item.setIsPendingSaleLocked("no");
        assertFalse(item.isPendingSaleLocked());

        item.setIsPendingSaleLocked(null);
        assertFalse(item.isPendingSaleLocked());
    }

    @Test
    void shouldConvertPositiveMillisToDate() {
        ListingItem item = new ListingItem();
        item.setRegistrationDateMillis(1609459200000L);
        item.setExpirationDateMillis(1640995200000L);

        assertEquals(1609459200000L, item.getRegistrationDate().getTime());
        assertEquals(1640995200000L, item.getExpirationDate().getTime());
    }

    @Test
    void shouldReturnNullDateWhenMissingOrZero() {
        ListingItem item = new ListingItem();
        assertNull(item.getRegistrationDate());
        assertNull(item.getExpirationDate());

        item.setRegistrationDateMillis(0L);
        item.setExpirationDateMillis(0L);
        assertNull(item.getRegistrationDate());
        assertNull(item.getExpirationDate());
    }

    @Test
    void responseShouldExposeEmptyListByDefault() {
        assertTrue(new ListingListResponse().getItems().isEmpty());
    }
}
