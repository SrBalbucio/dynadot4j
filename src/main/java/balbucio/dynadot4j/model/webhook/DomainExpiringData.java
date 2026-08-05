package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DomainExpiringData {

    @SerializedName("domains_expired_after_30_days")
    private String domainsExpiredAfter30Days;
    @SerializedName("domains_expired_after_10_days")
    private String domainsExpiredAfter10Days;
    @SerializedName("domains_expired_after_3_days")
    private String domainsExpiredAfter3Days;
    @SerializedName("domains_expired_today")
    private String domainsExpiredToday;
    @SerializedName("domains_redemption")
    private String domainsRedemption;
}