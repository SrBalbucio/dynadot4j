package balbucio.dynadot4j;

import balbucio.dynadot4j.model.AccountPriceLevel;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.*;

class DynadotConfigTest {

    @Test
    void createDefaultShouldSetDefaults() {
        DynadotConfig config = Dynadot.createDefault()
                .apiKey("key")
                .apiSecret("secret")
                .build();

        assertNotNull(config.getExecutorService());
        assertEquals(AccountPriceLevel.REGULAR, config.getPriceLevel());
        assertEquals("https://api.dynadot.com", config.getEndpointUrl());
        assertFalse(config.isDebug());
        assertEquals(0, config.getRequestThreads());
    }

    @Test
    void builderShouldOverrideDefaults() {
        ScheduledExecutorService customExecutor = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
        DynadotConfig config = DynadotConfig.builder()
                .apiKey("custom-key")
                .apiSecret("custom-secret")
                .endpointUrl("https://custom.api.com")
                .priceLevel(AccountPriceLevel.BULK)
                .executorService(customExecutor)
                .requestThreads(5)
                .debug(true)
                .build();

        assertEquals("custom-key", config.getApiKey());
        assertEquals("custom-secret", config.getApiSecret());
        assertEquals("https://custom.api.com", config.getEndpointUrl());
        assertEquals(AccountPriceLevel.BULK, config.getPriceLevel());
        assertSame(customExecutor, config.getExecutorService());
        assertEquals(5, config.getRequestThreads());
        assertTrue(config.isDebug());
    }
}
