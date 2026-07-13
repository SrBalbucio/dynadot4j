package balbucio.dynadot4j;

import balbucio.dynadot4j.client.AccountClient;
import balbucio.dynadot4j.model.DynadotAccountInfo;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountClientIntegrationTest {

    private Dynadot dynadot;
    private AccountClient accountClient;

    @BeforeAll
    void beforeAll() {
        String apiKey = System.getenv("DYNADOT_APIKEY");
        String apiSecret = System.getenv("DYNADOT_APISECRET");

        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            throw new RuntimeException("DYNADOT_APIKEY and DYNADOT_APISECRET environment variables must be set");
        }

        DynadotConfig config = Dynadot.createDefault()
                .endpointUrl("https://api-sandbox.dynadot.com")
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

        this.dynadot = new Dynadot(config);
        this.accountClient = dynadot.getAccountClient();
    }

    @Test
    @DisplayName("Get Account Info")
    @Order(1)
    void getAccountInfo() {
        assertDoesNotThrow(() -> {
            DynadotAccountInfo accountInfo = accountClient.getAccountInfo().get();
            assertNotNull(accountInfo);
        });
    }
}
