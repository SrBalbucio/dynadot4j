package balbucio.dynadot4j.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuctionItemDetailsTest {

    private final Gson gson = new Gson();

    @Test
    void shouldParseIsDynadotVariants() {
        AuctionDetailsResponse rTrue = gson.fromJson("""
                {"auction_item_details":{"auction_id":1,"is_dynadot":"true"}}
                """, AuctionDetailsResponse.class);
        assertTrue(rTrue.getAuctionItemDetails().isDynadotAuction());

        AuctionDetailsResponse rYes = gson.fromJson("""
                {"auction_item_details":{"auction_id":1,"is_dynadot":"Yes"}}
                """, AuctionDetailsResponse.class);
        assertTrue(rYes.getAuctionItemDetails().isDynadotAuction());

        AuctionDetailsResponse rNo = gson.fromJson("""
                {"auction_item_details":{"auction_id":1,"is_dynadot":"No"}}
                """, AuctionDetailsResponse.class);
        assertFalse(rNo.getAuctionItemDetails().isDynadotAuction());

        AuctionDetailsResponse rMissing = gson.fromJson("""
                {"auction_item_details":{"auction_id":1}}
                """, AuctionDetailsResponse.class);
        assertFalse(rMissing.getAuctionItemDetails().isDynadotAuction());
        assertTrue(rMissing.getAuctionItemDetails().getBidHistory().isEmpty());
    }
}
