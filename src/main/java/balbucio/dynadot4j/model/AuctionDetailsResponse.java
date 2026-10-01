package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuctionDetailsResponse {

    @SerializedName("auction_item_details")
    private AuctionItemDetails auctionItemDetails;
}
