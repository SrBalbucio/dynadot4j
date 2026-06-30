package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountPriceLevelTest {

    @Test
    void regularShouldHaveCorrectValues() {
        assertEquals(1, AccountPriceLevel.REGULAR.getMaxRequestPerSec());
        assertEquals(5, AccountPriceLevel.REGULAR.getSearchLimit());
    }

    @Test
    void bulkShouldHaveCorrectValues() {
        assertEquals(60, AccountPriceLevel.BULK.getMaxRequestPerSec());
        assertEquals(10, AccountPriceLevel.BULK.getSearchLimit());
    }

    @Test
    void superBulkShouldHaveCorrectValues() {
        assertEquals(600, AccountPriceLevel.SUPERBULK.getMaxRequestPerSec());
        assertEquals(20, AccountPriceLevel.SUPERBULK.getSearchLimit());
    }
}
