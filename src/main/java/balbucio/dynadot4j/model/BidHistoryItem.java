package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BidHistoryItem {

    @SerializedName("bidder_name")
    private String bidderName;
    @SerializedName("bid_price")
    private String bidPrice;
    private String currency;
    private Long timestamp;
    @SerializedName("bid_status")
    private String bidStatus;
    @SerializedName("is_proxy_auto_bid")
    private String isProxyAutoBid;
}
