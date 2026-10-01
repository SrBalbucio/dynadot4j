package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.DynadotRequester;
import balbucio.dynadot4j.model.AccountPriceLevel;
import balbucio.dynadot4j.model.DynadotHttpResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AftermarketClientTest {

    @Mock
    private Dynadot dynadot;

    @Mock
    private DynadotRequester requester;

    @Captor
    private ArgumentCaptor<String> pathCaptor;

    @Captor
    private ArgumentCaptor<String> bodyCaptor;

    private Gson gson;
    private AftermarketClient client;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        DynadotConfig config = Dynadot.createDefault()
                .apiKey("key")
                .apiSecret("secret")
                .priceLevel(AccountPriceLevel.REGULAR)
                .build();

        lenient().when(dynadot.getConfig()).thenReturn(config);
        lenient().when(dynadot.getRequester()).thenReturn(requester);
        lenient().when(dynadot.getGson()).thenReturn(gson);

        client = new AftermarketClient(dynadot);
    }

    @Test
    void getAuctionDetailsShouldGetCorrectPathAndParseIsDynadot() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"auction_item_details":{"auction_id":12345,"domain_name":"example.com","is_dynadot":"true","max_installment_months":12}}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        var details = client.getAuctionDetails("example.com", "USD").get();

        verify(requester).get(eq("restful/v2/aftermarket/auctions/example.com?currency=USD"));
        assertNotNull(details);
        assertEquals(12345, details.getAuctionId());
        assertEquals("example.com", details.getDomainName());
        assertEquals("true", details.getIsDynadot());
        assertTrue(details.isDynadotAuction());
        assertEquals(12, details.getMaxInstallmentMonths());
    }

    @Test
    void getAuctionDetailsShouldHandleMissingIsDynadot() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"auction_item_details":{"auction_id":1,"domain_name":"example.com"}}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        var details = client.getAuctionDetails("example.com").get();

        assertNotNull(details);
        assertNull(details.getIsDynadot());
        assertFalse(details.isDynadotAuction());
    }

    @Test
    void placeAuctionBidShouldPostCorrectPathAndBody() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"auction_item_details":{"auction_id":12345,"domain_name":"example.com","is_dynadot":"Yes"}}}
                """, DynadotHttpResponse.class);
        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        var details = client.placeAuctionBid("example.com", 150.0, "USD", false).get();

        verify(requester).post(eq("restful/v2/aftermarket/auctions/bids/example.com"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"bid_amount\":150"));
        assertTrue(bodyCaptor.getValue().contains("\"currency\":\"USD\""));
        assertTrue(bodyCaptor.getValue().contains("\"is_backorder_auction\":false"));
        assertNotNull(details);
        assertTrue(details.isDynadotAuction());
    }

    @Test
    void placeAuctionBidShouldPropagateError() {
        when(requester.post(anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("API error")));

        assertThrows(Exception.class, () -> client.placeAuctionBid("example.com", 10.0, "USD").get());
    }
}
