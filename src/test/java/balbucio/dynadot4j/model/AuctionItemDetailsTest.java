package balbucio.dynadot4j.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuctionItemDetailsTest {

    private final Gson gson = new Gson();

    @Test
    void shouldParseDocumentedFieldsAndBooleanVariants() {
        AuctionDetailsResponse response = gson.fromJson("""
                {"auction_item_details":{
                  "auction_id":12345,
                  "domain_name":"example.com",
                  "current_bid_price":"150.00",
                  "is_high_bidder":"Yes",
                  "auction_ended":"No",
                  "is_premium":"Yes",
                  "installment_status":"active",
                  "max_installment_months":12,
                  "bid_history_item_list":[
                    {"bidder_name":"Bidder1","bid_price":"100.00","currency":"USD","timestamp":0,"bid_status":"Active","is_proxy_auto_bid":"Yes"}
                  ]
                }}
                """, AuctionDetailsResponse.class);

        var details = response.getAuctionItemDetails();
        assertEquals(12345, details.getAuctionId());
        assertEquals("example.com", details.getDomainName());
        assertTrue(details.isHighBidder());
        assertFalse(details.isAuctionEnded());
        assertTrue(details.isPremium());
        assertEquals("active", details.getInstallmentStatus());
        assertEquals(12, details.getMaxInstallmentMonths());
        assertEquals(1, details.getBidHistory().size());
        assertEquals("Bidder1", details.getBidHistory().get(0).getBidderName());
    }

    @Test
    void shouldDefaultMissingOptionalFields() {
        AuctionDetailsResponse response = gson.fromJson("""
                {"auction_item_details":{"auction_id":1}}
                """, AuctionDetailsResponse.class);

        var details = response.getAuctionItemDetails();
        assertFalse(details.isHighBidder());
        assertFalse(details.isAuctionEnded());
        assertFalse(details.isPremium());
        assertTrue(details.getBidHistory().isEmpty());
    }
}
