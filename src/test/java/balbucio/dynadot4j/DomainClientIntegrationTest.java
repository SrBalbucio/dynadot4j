package balbucio.dynadot4j;

import balbucio.dynadot4j.action.DomainRegistration;
import balbucio.dynadot4j.client.DomainClient;
import balbucio.dynadot4j.model.*;
import org.junit.jupiter.api.*;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DomainClientIntegrationTest {

    private Dynadot dynadot;
    private DomainClient domainClient;
    private String domainName;

    @BeforeAll
    void beforeAll() throws NoSuchAlgorithmException {
        String apiKey = System.getenv("DYNADOT_APIKEY");
        String apiSecret = System.getenv("DYNADOT_APISECRET");

        if (apiKey == null || apiKey.isEmpty() || apiSecret == null || apiSecret.isEmpty()) {
            throw new RuntimeException("DYNADOT_APIKEY and DYNADOT_APISECRET must be set");
        }

        this.domainName = System.getenv("DYNADOT_DOMAINNAME");
        if (this.domainName == null || this.domainName.isEmpty()) {
            this.domainName = "example.com";
        }

        DynadotConfig config = Dynadot.createDefault()
                .endpointUrl("https://api-sandbox.dynadot.com")
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .debug(true)
                .build();

        this.dynadot = new Dynadot(config);
        this.domainClient = dynadot.getDomainClient();
    }

    private boolean registered;

    @Test
    @DisplayName("Search Domain (available)")
    @Order(1)
    void searchDomain() {
        assertDoesNotThrow(() -> {
            DomainSearchResult result = domainClient.search(domainName, "BRL").get();
            assertNotNull(result);

            registered = !result.isAvailable();
            Optional<DomainPriceEntry> oneYear = result.getPriceByYearPeriod(1);
            assertTrue(oneYear.isPresent());
            assertTrue(oneYear.get().registrationPriceAsDouble() > 0.0);
        });
    }

    @Test
    @DisplayName("Bulk Search Domain")
    @Order(2)
    void bulkSearchDomain() {
        assertDoesNotThrow(() -> {
            List<BulkSearchResult> result = domainClient.searchBulk(domainName, "USD").get();
            assertNotNull(result);
            assertFalse(result.isEmpty());
        });
    }

    @Test
    @DisplayName("Get Suggestions")
    @Order(3)
    void getSuggestions() {
        assertDoesNotThrow(() -> {
            List<String> result = domainClient.getSuggestionSearch(domainName, List.of("com", "net")).get();
            assertNotNull(result);
        });
    }

    private DomainRegisterResult registeredDomain;

    @Test
    @DisplayName("Register Domain")
    @Order(4)
    void registerDomain() {
        assertDoesNotThrow(() -> {
            if (!registered) {
                registeredDomain = domainClient.register(DomainRegistration.create(domainName)
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
                assertNotNull(registeredDomain);
            }
        });
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Test
    @DisplayName("Renew Domain")
    @Order(5)
    void renewDomain() {
        assertDoesNotThrow(() -> {
            if (!registered) {
                Long result = domainClient.renew(domainName, 1, registeredDomain).get();
                assertTrue(result > 0);
                Date expirationDate = new Date(result);
                assertNotNull(sdf.format(expirationDate));
            }
        });
    }

    @Test
    @DisplayName("Set NS")
    @Order(6)
    void setNameservers() {
        assertDoesNotThrow(() -> {
            if (!registered) {
                domainClient.setNameservers(domainName, List.of("ns1.example.net", "ns2.example.net")).get();
            }
        });
    }

    @Test
    @DisplayName("Set Parking")
    @Order(7)
    void setParking() {
        assertDoesNotThrow(() -> {
            if (!registered) {
                domainClient.setParking(domainName, false).get();
            }
        });
    }

    @Test
    @DisplayName("Set Privacy")
    @Order(8)
    void setPrivacy() {
        assertDoesNotThrow(() -> {
            if (!registered) {
                domainClient.setPrivacy(domainName, DomainPrivacy.FULL, true).get();
            }
        });
    }

    @Test
    @DisplayName("Set Forwarding")
    @Order(9)
    void setForwarding() {
        assertDoesNotThrow(() -> {
            if (!registered) {
                domainClient.setForwarding(domainName, "https://discord.gg", false).get();
            }
        });
    }

    @Test
    @DisplayName("Set Renew Option")
    @Order(10)
    void setRenewOption() {
        assertDoesNotThrow(() -> {
            if (!registered) {
                domainClient.setRenewOption(domainName, DomainRenewOption.AUTO);
            }
        });
    }

    @Test
    @DisplayName("Get Domain Info")
    @Order(11)
    void getDomainInfo() {
        assertDoesNotThrow(() -> {
            if (!registered) {
                DomainInfo domainInfo = domainClient.getDomain(domainName).get();
                assertNotNull(domainInfo);
                assertEquals(domainName, domainInfo.getDomainName());
            }
        });
    }
}
