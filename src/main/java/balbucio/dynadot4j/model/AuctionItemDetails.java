package balbucio.dynadot4j.model;

import balbucio.dynadot4j.utils.DynadotConvertUtils;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Detalhes de um item de leilão (aftermarket) retornado pela API Dynadot v2.
 *
 * <p>Cobre {@code get_auction_details} ({@code GET /restful/v2/aftermarket/auctions/{domain_name}})
 * e {@code place_auction_bid} ({@code POST /restful/v2/aftermarket/auctions/bids/{domain_name}}),
 * ambos com envelope {@code data.auction_item_details}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuctionItemDetails {

    @SerializedName("auction_id")
    private Integer auctionId;
    @SerializedName("domain_name")
    private String domainName;
    @SerializedName("utf_domain")
    private String utfDomain;
    @SerializedName("is_idn")
    private String isIdn;
    @SerializedName("auction_type")
    private String auctionType;
    @SerializedName("current_bid_price")
    private String currentBidPrice;
    @SerializedName("accepted_bid_price")
    private String acceptedBidPrice;
    private String currency;
    @SerializedName("is_high_bidder")
    private String isHighBidder;
    private Integer bids;
    private Integer bidders;
    @SerializedName("auction_status_id")
    private Integer auctionStatusId;
    @SerializedName("time_left")
    private String timeLeft;
    @SerializedName("start_time")
    private String startTime;
    @SerializedName("start_time_stamp")
    private Long startTimeStamp;
    @SerializedName("end_time")
    private String endTime;
    @SerializedName("end_time_stamp")
    private Long endTimeStamp;
    private String revenue;
    private Long visitors;
    private String links;
    private Integer age;
    @SerializedName("estibot_appraisal")
    private String estibotAppraisal;
    @SerializedName("dyna_appraisal")
    private String dynaAppraisal;
    @SerializedName("auction_ended")
    private String auctionEnded;
    @SerializedName("customer_bided")
    private String customerBided;
    @SerializedName("customer_bid")
    private String customerBid;
    @SerializedName("customer_proxy_bid")
    private String customerProxyBid;
    @SerializedName("is_premium")
    private String isPremium;
    @SerializedName("renewal_price")
    private String renewalPrice;
    @SerializedName("revenue_currency")
    private String revenueCurrency;
    @SerializedName("start_price")
    private String startPrice;
    @SerializedName("bid_history_item_list")
    private List<BidHistoryItem> bidHistoryItemList;
    @SerializedName("auction_status_name")
    private String auctionStatusName;
    @SerializedName("installment_status")
    private String installmentStatus;
    @SerializedName("max_installment_months")
    private Integer maxInstallmentMonths;
    @SerializedName("is_dynadot")
    private String isDynadot;

    public boolean isDynadotAuction() {
        return DynadotConvertUtils.asBool(isDynadot);
    }

    public boolean isHighBidder() {
        return DynadotConvertUtils.asBool(isHighBidder);
    }

    public boolean isAuctionEnded() {
        return DynadotConvertUtils.asBool(auctionEnded);
    }

    public boolean isPremium() {
        return DynadotConvertUtils.asBool(isPremium);
    }

    public List<BidHistoryItem> getBidHistory() {
        return bidHistoryItemList != null ? bidHistoryItemList : new ArrayList<>();
    }
}
