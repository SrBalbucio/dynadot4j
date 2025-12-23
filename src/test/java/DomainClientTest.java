import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.action.DomainRegistration;
import balbucio.dynadot4j.client.DomainClient;
import balbucio.dynadot4j.model.*;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        this.domainName = JOptionPane.showInputDialog(null,
                "Qual será o domínio utilizado para testes?",
                "Dynadot4j Test Unit",
                JOptionPane.QUESTION_MESSAGE);

        if (this.domainName == null || this.domainName.isEmpty()) {
            this.domainName = System.getenv("DYNADOT_DOMAINNAME");
        }

        DynadotConfig config = DynadotConfig.createDefault()
                .endpointUrl("https://api-sandbox.dynadot.com")
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

        this.dynadot = new Dynadot(config);
        this.domainClient = dynadot.getDomainClient();
    }

    private boolean registered;

    @Test
    @DisplayName("Search Domain (avaliable)")
    @Order(1)
    public void searchDomain() {
        assertDoesNotThrow(() -> {
            DomainSearchResult result = domainClient.search(domainName, "USD").get();
            assertNotNull(result);

            registered = !result.isAvailable();
            Optional<DomainPriceEntry> oneYear = result.getPriceByYearPeriod(1);
            assertTrue(oneYear.isPresent());
            assertTrue(oneYear.get().registrationPriceAsDouble() > 0.0);
            System.out.println(oneYear.get());
        });
    }


    @Test
    @DisplayName("Bulk Search Domain (avaliable)")
    @Order(1)
    public void bulkSearchDomain() {
        assertDoesNotThrow(() -> {
            List<DomainSearchResult> result = domainClient.searchBulk(domainName, "USD").get();
            assertNotNull(result);
            assertFalse(result.isEmpty());
        });
    }

    @Test
    @DisplayName("Get Suggestions")
    @Order(2)
    public void getSuggestions() {
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
    public void registerDomain() {
        assertDoesNotThrow(() -> {
            if (!registered) {
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
                        .withPrivacy(DomainPrivacy.FULL)
                ).get();
                assertNotNull(result);
                System.out.println(result);
            }
        });
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Test
    @DisplayName("Renew Domain")
    @Order(4)
    public void renewDomain() {
        assertDoesNotThrow(() -> {
            if (!registered) {
                Long result = domainClient.renew(domainName, 1, 2026).get();
                assertTrue(result > 0);
                System.out.println(result);

                Date expirationDate = new Date(result);
                System.out.println(sdf.format(expirationDate));
            }
        });
    }


    @Test
    @DisplayName("Set NS")
    @Order(5)
    public void setNameservers() {
        assertDoesNotThrow(() -> {
            domainClient.setNameservers(domainName, List.of("ns1.example.net", "ns2.example.net")).get();
        });
    }

    @Test
    @DisplayName("Set Parking")
    @Order(6)
    public void setParking() {
        assertDoesNotThrow(() -> {
            domainClient.setParking(domainName, false).get();
        });
    }

    @Test
    @DisplayName("Set Privacy")
    @Order(7)
    public void setPrivacy() {
        assertDoesNotThrow(() -> {
            domainClient.setPrivacy(domainName, DomainPrivacy.FULL, true).get();
        });
    }

    @Test
    @DisplayName("Set Forwarding")
    @Order(8)
    public void setForwarding() {
        assertDoesNotThrow(() -> {
            domainClient.setForwarding(domainName, "https://discord.gg", false).get();
        });
    }

    @Test
    @DisplayName("Set Renew Option")
    @Order(9)
    public void setRenewOption() {
        assertDoesNotThrow(() -> {
            domainClient.setRenewOption(domainName, DomainRenewOption.AUTO);
        });
    }

    @Test
    @DisplayName("Get Domain Info")
    @Order(10)
    public void getDomainInfo() {
        assertDoesNotThrow(() -> {
            DomainInfo domainInfo = domainClient.getDomain(domainName).get();
            assertNotNull(domainInfo);
            System.out.println(domainInfo);
            assertEquals(domainName, domainInfo.getDomainName());
        });
    }

}
