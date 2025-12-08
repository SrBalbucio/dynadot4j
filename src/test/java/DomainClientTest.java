import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.action.DomainRegistration;
import balbucio.dynadot4j.client.DomainClient;
import balbucio.dynadot4j.model.DomainPriceEntry;
import balbucio.dynadot4j.model.DomainRegisterResult;
import balbucio.dynadot4j.model.DomainSearchResult;
import balbucio.dynadot4j.model.RegistrantContact;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DomainClientTest {

    private Dynadot dynadot;
    private DomainClient domainClient;
    private String domainName;

    @BeforeAll
    public void beforeAll() {
        String apiKey = System.getenv("DYNADOT_APIKEY");
        String apiSecret = System.getenv("DYNADOT_APISECRET");
        // defina qualquer um a não ser que esteja utilizando a Key de produção (loucura inclusive)
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
            assertTrue(oneYear.get().registrationPriceAsDouble() > 0.0);
            System.out.println(oneYear.get());
        });
    }

    @Test
    @DisplayName("Get Suggestions")
    @Order(2)
    public void getSuggestions(){
        assertDoesNotThrow(() -> {
            List<String> result = domainClient.getSuggestionSearch(domainName, List.of("com", "net")).get();
            assertNotNull(result);
            assertTrue(result.isEmpty());
            System.out.println(result);
        });
    }

    @Test
    @DisplayName("Register Domain")
    @Order(3)
    public void registerDomain(){
        assertDoesNotThrow(() -> {
            DomainRegisterResult result = domainClient.register(DomainRegistration.create(domainName)
                    .withContact(RegistrantContact.builder()
                            .name("Aleskib")
                            .email("example@email.com")
                            .city("São Paulo")
                            .state("SP")
                            .address("Rua da Realeza, 2989")
                            .country("Brazil")
                            .organization("NAVI")
                            .phoneCC("55")
                            .phoneNumber("9934820745")
                            .build())
                    .withCustomerId(0)
                    .withDuration(1)
                    .addNS("ns1.example.com")
                    .addNS("ns2.example.com")
            ).get();
            assertNotNull(result);
            System.out.println(result);
        });
    }
}
