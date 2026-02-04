import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.client.AccountClient;
import balbucio.dynadot4j.model.AccountPriceLevel;
import balbucio.dynadot4j.model.DynadotAccountInfo;
import org.junit.jupiter.api.*;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountClientTest {

    private Dynadot dynadot;
    private AccountClient accountClient;

    @BeforeAll
    public void beforeAll() {
        String apiKey = System.getenv("DYNADOT_APIKEY");
        String apiSecret = System.getenv("DYNADOT_APISECRET");

        DynadotConfig config = DynadotConfig.createDefault()
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
    public void getAccountIndo() {
        assertDoesNotThrow(() -> {
            DynadotAccountInfo accountInfo = accountClient.getAccountInfo().get();

            assertNotNull(accountInfo);
            System.out.println(accountInfo);
        });
    }

}
