package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.DynadotRequester;
import balbucio.dynadot4j.model.AccountPriceLevel;
import balbucio.dynadot4j.model.DynadotHttpResponse;
import balbucio.dynadot4j.model.ListingItem;
import balbucio.dynadot4j.model.ListingListResponse;
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
    void getAuctionDetailsShouldGetCorrectPathAndParseAuctionItemDetails() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"auction_item_details":{"auction_id":12345,"domain_name":"example.com","max_installment_months":12}}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        var details = client.getAuctionDetails("example.com", "USD").get();

        verify(requester).get(eq("restful/v2/aftermarket/auctions/example.com?currency=USD"));
        assertNotNull(details);
        assertEquals(12345, details.getAuctionId());
        assertEquals("example.com", details.getDomainName());
        assertEquals(12, details.getMaxInstallmentMonths());
    }

    @Test
    void getAuctionDetailsShouldHandleMissingOptionalFields() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"auction_item_details":{"auction_id":1,"domain_name":"example.com"}}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        var details = client.getAuctionDetails("example.com").get();

        assertNotNull(details);
        assertEquals(1, details.getAuctionId());
        assertTrue(details.getBidHistory().isEmpty());
    }

    @Test
    void placeAuctionBidShouldPostCorrectPathAndBody() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"auction_item_details":{"auction_id":12345,"domain_name":"example.com"}}}
                """, DynadotHttpResponse.class);
        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        var details = client.placeAuctionBid("example.com", 150.0, "USD", false).get();

        verify(requester).post(eq("restful/v2/aftermarket/auctions/bids/example.com"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"bid_amount\":150"));
        assertTrue(bodyCaptor.getValue().contains("\"currency\":\"USD\""));
        assertTrue(bodyCaptor.getValue().contains("\"is_backorder_auction\":false"));
        assertNotNull(details);
        assertEquals("example.com", details.getDomainName());
    }

    @Test
    void placeAuctionBidShouldPropagateError() {
        when(requester.post(anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("API error")));

        assertThrows(Exception.class, () -> client.placeAuctionBid("example.com", 10.0, "USD").get());
    }

    @Test
    void getListingsShouldSendRequiredPagingParams() throws Exception {
        DynadotHttpResponse response = listingResponse();
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.getListings("usd", 2, 50).get();

        verify(requester).get(eq(
                "restful/v2/aftermarket/listings?currency=USD&page=2&page_size=50"));
    }

    @Test
    void getListingsShouldIncludeOptionalFlagsOnlyWhenProvided() throws Exception {
        DynadotHttpResponse response = listingResponse();
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.getListings("USD", 1, 10, true, false).get();

        verify(requester).get(eq(
                "restful/v2/aftermarket/listings?currency=USD&page=1&page_size=10"
                        + "&exclude_pending_sale=true&show_other_registrar=false"));
    }

    @Test
    void getListingsShouldParseItemsAndTotal() throws Exception {
        DynadotHttpResponse response = listingResponse();
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        ListingListResponse listings = client.getListings("USD", 1, 10).get();

        assertEquals(10L, listings.getTotal());
        assertEquals(1, listings.getItems().size());

        ListingItem item = listings.getItems().get(0);
        assertEquals(1, item.getListingId());
        assertEquals("example.com", item.getDomain());
        assertEquals("$12.88", item.getPrice());
        assertEquals(2, item.getInBoundLinks());
        assertEquals(8, item.getAge());
        assertEquals("BIN", item.getType());
        assertFalse(item.isPendingSaleLocked());
        assertNull(item.getRegistrationDate());
        assertNull(item.getExpirationDate());
    }

    @Test
    void getListingsShouldInterpretYesAsPendingSaleLocked() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"total":1,"listing_item_list":[
                  {"listing_id":7,"domain":"locked.com","is_pending_sale_locked":"Yes",
                   "registration_date":1609459200000,"expiration_date":1640995200000}
                ]}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        ListingItem item = client.getListings("USD", 1, 10).get().getItems().get(0);

        assertTrue(item.isPendingSaleLocked());
        assertNotNull(item.getRegistrationDate());
        assertNotNull(item.getExpirationDate());
    }

    @Test
    void getListingsShouldReturnEmptyListWhenAbsent() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"total":0}}
                """, DynadotHttpResponse.class);
        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        ListingListResponse listings = client.getListings("USD", 1, 10).get();

        assertTrue(listings.getItems().isEmpty());
    }

    @Test
    void getListingsShouldRejectInvalidPaging() {
        assertThrows(IllegalArgumentException.class, () -> client.getListings("USD", 0, 10));
        assertThrows(IllegalArgumentException.class, () -> client.getListings("USD", 1, 0));
        assertThrows(IllegalArgumentException.class, () -> client.getListings("USD", 1, 20001));
        assertThrows(IllegalArgumentException.class, () -> client.getListings("", 1, 10));
        verifyNoInteractions(requester);
    }

    private DynadotHttpResponse listingResponse() {
        return gson.fromJson("""
                {"data":{"total":10,"listing_item_list":[
                  {"listing_id":1,"domain":"example.com","price":"$12.88","in_bound_links":2,
                   "age":8,"is_pending_sale_locked":"No","type":"BIN",
                   "registration_date":0,"expiration_date":0}
                ]}}
                """, DynadotHttpResponse.class);
    }
}
