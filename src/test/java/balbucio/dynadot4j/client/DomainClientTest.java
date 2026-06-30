package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.DynadotRequester;
import balbucio.dynadot4j.action.DomainRegistration;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomainClientTest {

    @Mock
    private Dynadot dynadot;

    @Mock
    private DynadotRequester requester;

    @Captor
    private ArgumentCaptor<String> pathCaptor;

    @Captor
    private ArgumentCaptor<String> bodyCaptor;

    private Gson gson;
    private DomainClient client;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        DynadotConfig config = DynadotConfig.createDefault()
                .apiKey("key")
                .apiSecret("secret")
                .priceLevel(AccountPriceLevel.REGULAR)
                .build();

        lenient().when(dynadot.getConfig()).thenReturn(config);
        lenient().when(dynadot.getRequester()).thenReturn(requester);
        lenient().when(dynadot.getGson()).thenReturn(gson);

        client = new DomainClient(dynadot);
    }

    @Test
    void searchShouldCallCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_name":"example.com","available":"Yes","premium":"no","show_price":"yes","price_list":[{"currency":"USD","unit":"(1 year)","registration_price":"$10.00","renewal_price":"$12.00"}]}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        DomainSearchResult result = client.search("example.com", "USD").get();

        assertNotNull(result);
        assertTrue(result.isAvailable());
        verify(requester).get(pathCaptor.capture());
        assertTrue(pathCaptor.getValue().contains("restful/v2/domains/example.com/search"));
        assertTrue(pathCaptor.getValue().contains("currency=USD"));
    }

    @Test
    void searchShouldThrowForEmptyDomain() {
        assertThrows(InvalidDomainException.class, () -> client.search("", "USD"));
    }

    @Test
    void searchShouldDefaultToUsdCurrency() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_name":"example.com","available":"Yes","premium":"no","show_price":"yes","price_list":[]}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.search("example.com", null).get();
        verify(requester).get(pathCaptor.capture());
        assertTrue(pathCaptor.getValue().contains("currency=USD"));
    }

    @Test
    void searchBulkWithSingleDomainShouldExpandAndCallCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_result_list":[]}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        List<BulkSearchResult> results = client.searchBulk("example.com", "USD").get();
        assertNotNull(results);
        verify(requester).get(pathCaptor.capture());
        assertTrue(pathCaptor.getValue().contains("restful/v2/domains/bulk_search"));
        assertTrue(pathCaptor.getValue().contains("example.net"));
        assertTrue(pathCaptor.getValue().contains("example.com"));
    }

    @Test
    void searchBulkWithListShouldCallCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_result_list":[{"domain_name":"test.com","available":"Yes"},{"domain_name":"test.net","available":"no"}]}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        List<BulkSearchResult> results = client.searchBulk(List.of("test.com", "test.net"), "USD").get();

        assertEquals(2, results.size());
        assertTrue(results.get(0).isAvailable());
        assertFalse(results.get(1).isAvailable());
    }

    @Test
    void searchBulkWithEmptyListShouldThrow() {
        assertThrows(InvalidDomainException.class, () -> client.searchBulk(List.of(), "USD"));
    }

    @Test
    void getSuggestionSearchShouldReturnList() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_list":["test.com","test.net"]}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        List<String> suggestions = client.getSuggestionSearch("test", List.of("com", "net")).get();

        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains("test.com"));
        verify(requester).get(pathCaptor.capture());
        assertTrue(pathCaptor.getValue().contains("suggestion_search"));
    }

    @Test
    void getSuggestionSearchShouldThrowForEmptyDomain() {
        assertThrows(InvalidDomainException.class, () -> client.getSuggestionSearch("", List.of("com")));
    }

    @Test
    void getSuggestionSearchShouldDefaultToCom() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_list":[]}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.getSuggestionSearch("test", new java.util.ArrayList<>()).get();
        verify(requester).get(pathCaptor.capture());
        assertTrue(pathCaptor.getValue().contains("tlds=com"));
    }

    @Test
    void getSuggestionSearchShouldReturnEmptyOnException() throws Exception {
        when(requester.get(anyString())).thenReturn(CompletableFuture.failedFuture(new RuntimeException()));

        List<String> suggestions = client.getSuggestionSearch("test", List.of("com")).get();
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void registerShouldPostCorrectPathAndBody() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_name":"example.com","expiration_date":1767225600000}}
                """, DynadotHttpResponse.class);
        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        DomainRegistration action = DomainRegistration.create("example.com")
                .withContact(RegistrantContact.builder().name("John").email("j@j.com")
                        .phoneNumber("123").phoneCC("55").address("Addr")
                        .city("City").state("ST").country("BR").build());

        DomainRegisterResult result = client.register(action).get();

        assertEquals("example.com", result.getDomainName());
        verify(requester).post(eq("restful/v2/domains/example.com/register"), anyString());
    }

    @Test
    void renewWithExplicitParamsShouldPostCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"expiration_date":1767225600000}}
                """, DynadotHttpResponse.class);
        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        long result = client.renew("example.com", 1, 2026, false).get();
        assertTrue(result > 0);

        verify(requester).post(eq("restful/v2/domains/example.com/renew"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"duration\":1"));
        assertTrue(bodyCaptor.getValue().contains("\"year\":2026"));
    }

    @Test
    void renewWithYearOnlyShouldDefaultNoLateFee() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"expiration_date":1767225600000}}
                """, DynadotHttpResponse.class);
        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.renew("example.com", 1, 2026).get();
        verify(requester).post(anyString(), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"no_renew_if_late_renew_fee_needed\":false"));
    }

    @Test
    void renewWithDateShouldExtractYear() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"expiration_date":1767225600000}}
                """, DynadotHttpResponse.class);
        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        Date date = new Date(1767225600000L);
        int expectedYear = date.getYear();
        client.renew("example.com", 1, date).get();
        verify(requester).post(anyString(), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"year\":" + expectedYear));
    }

    @Test
    void setNameserversShouldPutCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"code":"200","message":"OK","data":{}}
                """, DynadotHttpResponse.class);
        when(requester.put(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.setNameservers("example.com", List.of("ns1.example.net", "ns2.example.net")).get();

        verify(requester).put(eq("restful/v2/domains/example.com/nameservers"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("ns1.example.net"));
    }

    @Test
    void setParkingShouldPutCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{}}
                """, DynadotHttpResponse.class);
        when(requester.put(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.setParking("example.com", true).get();

        verify(requester).put(eq("restful/v2/domains/example.com/parking"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"with_ads\":true"));
    }

    @Test
    void setPrivacyShouldPutCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{}}
                """, DynadotHttpResponse.class);
        when(requester.put(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.setPrivacy("example.com", DomainPrivacy.FULL, true).get();

        verify(requester).put(eq("restful/v2/domains/example.com/privacy"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"privacy_level\":\"full\""));
        assertTrue(bodyCaptor.getValue().contains("\"whois_privacy_option\":true"));
    }

    @Test
    void setForwardingShouldPutCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{}}
                """, DynadotHttpResponse.class);
        when(requester.put(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.setForwarding("example.com", "https://example.net", false).get();

        verify(requester).put(eq("restful/v2/domains/example.com/domain_forwarding"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"forward_url\":\"https://example.net\""));
        assertTrue(bodyCaptor.getValue().contains("\"is_temporary\":false"));
    }

    @Test
    void setDNSSECShouldPutCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{}}
                """, DynadotHttpResponse.class);
        when(requester.put(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.setDNSSEC("example.com", DNSSECAlgorithm.RSASHA256, "abcd", DigestType.SHA256, 12345, "pubkey").get();

        verify(requester).put(eq("restful/v2/domains/example.com/dnssec"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"digest\":\"abcd\""));
        assertTrue(bodyCaptor.getValue().contains("\"digest_type\":\"sha256\""));
        assertTrue(bodyCaptor.getValue().contains("\"algorithm\":\"rsasha256\""));
        assertTrue(bodyCaptor.getValue().contains("\"key_tag\":12345"));
    }

    @Test
    void clearDNSSECShouldDeleteCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{}}
                """, DynadotHttpResponse.class);
        when(requester.del(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.clearDNSSEC("example.com").get();

        verify(requester).del("restful/v2/domains/example.com/dnssec");
    }

    @Test
    void setRenewOptionShouldPutCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{}}
                """, DynadotHttpResponse.class);
        when(requester.put(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.setRenewOption("example.com", DomainRenewOption.AUTO).get();

        verify(requester).put(eq("restful/v2/domains/example.com/renew_option"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"renew_option\":\"auto\""));
    }

    @Test
    void getDomainShouldReturnDomainInfo() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_info":{"domain_name":"example.com","locked":"no","available":"Yes"}}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        DomainInfo info = client.getDomain("example.com").get();

        assertNotNull(info);
        assertEquals("example.com", info.getDomainName());
        verify(requester).get("restful/v2/domains/example.com");
    }

    @Test
    void searchBulkShouldRespectSearchLimit() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_result_list":[]}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        List<String> manyDomains = List.of("a.com", "b.com", "c.com", "d.com", "e.com", "f.com");
        client.searchBulk(manyDomains, "USD").get();

        verify(requester).get(pathCaptor.capture());
        String path = pathCaptor.getValue();
        assertTrue(path.contains("domain_name_list="));
    }
}
