package balbucio.dynadot4j;

import balbucio.dynadot4j.client.AccountClient;
import balbucio.dynadot4j.client.ContactClient;
import balbucio.dynadot4j.client.DomainClient;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynadotTest {

    private Dynadot createDynadot() {
        DynadotConfig config = Dynadot.createDefault()
                .apiKey("test-key")
                .apiSecret("test-secret")
                .endpointUrl("https://api.dynadot.com")
                .build();
        return new Dynadot(config);
    }

    @Test
    void constructorShouldThrowWhenApiKeyIsNull() {
        DynadotConfig config = Dynadot.createDefault()
                .apiKey(null)
                .apiSecret("secret")
                .build();
        assertThrows(NullPointerException.class, () -> new Dynadot(config));
    }

    @Test
    void constructorShouldThrowWhenApiSecretIsNull() {
        DynadotConfig config = Dynadot.createDefault()
                .apiKey("key")
                .apiSecret(null)
                .build();
        assertThrows(NullPointerException.class, () -> new Dynadot(config));
    }

    @Test
    void constructorShouldThrowWhenBothAreNull() {
        DynadotConfig config = Dynadot.createDefault()
                .apiKey(null)
                .apiSecret(null)
                .build();
        assertThrows(NullPointerException.class, () -> new Dynadot(config));
    }

    @Test
    void constructorShouldCreateClients() {
        Dynadot dynadot = createDynadot();
        assertNotNull(dynadot.getDomainClient());
        assertNotNull(dynadot.getContactClient());
        assertNotNull(dynadot.getAccountClient());

        assertInstanceOf(DomainClient.class, dynadot.getDomainClient());
        assertInstanceOf(ContactClient.class, dynadot.getContactClient());
        assertInstanceOf(AccountClient.class, dynadot.getAccountClient());
    }

    @Test
    void constructorShouldCreateRequester() {
        Dynadot dynadot = createDynadot();
        assertNotNull(dynadot.getRequester());
        assertInstanceOf(DynadotRequester.class, dynadot.getRequester());
    }

    @Test
    void constructorShouldCreateGson() {
        Dynadot dynadot = createDynadot();
        assertNotNull(dynadot.getGson());
        assertInstanceOf(Gson.class, dynadot.getGson());
    }

    @Test
    void configShouldBeAccessible() {
        Dynadot dynadot = createDynadot();
        assertNotNull(dynadot.getConfig());
        assertEquals("test-key", dynadot.getConfig().getApiKey());
    }
}
