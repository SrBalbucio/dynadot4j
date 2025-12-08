import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.client.DomainClient;
import balbucio.dynadot4j.model.DomainPriceEntry;
import balbucio.dynadot4j.model.DomainSearchResult;
import org.junit.jupiter.api.*;

import java.util.Optional;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DynadotTest {

    private Dynadot dynadot;
    private DomainClient domainClient;
    private String domainName;

    @BeforeAll
    public void beforeAll() {
        String apiKey = System.getenv("DYNADOT_APIKEY");
        String apiSecret = System.getenv("DYNADOT_APISECRET");
        // pra fazer vc pesquisar se está disponível
        this.domainName = System.getenv("DYNADOT_DOMAINNAME");

        DynadotConfig config = DynadotConfig.createDefault()
                .endpointUrl("https://api-sandbox.dynadot.com")
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

        this.dynadot = new Dynadot(config);
        this.domainClient = dynadot.getDomainClient();
    }

    @Test
    @DisplayName("Search Domain (avaliable)")
    @Order(1)
    public void searchDomain() {
        assertDoesNotThrow(() -> {
            DomainSearchResult result = domainClient.search(domainName, "USD").get();
            assertNotNull(result);

            // verifique no site antes testar
            assertTrue(result.isAvailable());
            Optional<DomainPriceEntry> oneYear = result.getPriceByYearPeriod(1);
            assertTrue(oneYear.isPresent());
            System.out.println(oneYear.get());
        });
    }
}
