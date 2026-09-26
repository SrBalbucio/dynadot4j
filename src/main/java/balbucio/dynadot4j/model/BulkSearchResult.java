package balbucio.dynadot4j.model;

import balbucio.dynadot4j.utils.DynadotConvertUtils;
import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BulkSearchResult {

    @SerializedName("domain_name")
    private String domainName;
    private String available;
    private String premium;
    @SerializedName("price_list")
    private List<DomainPriceEntry> priceList;
    @SerializedName("details_error_message")
    private String detailsErrorMessage;
    private String source;
    private String confidence;

    public BulkSearchResult(String domainName, String available, String premium, List<DomainPriceEntry> priceList) {
        this.domainName = domainName;
        this.available = available;
        this.premium = premium;
        this.priceList = priceList;
    }

    public boolean isAvailable() {
        return DynadotConvertUtils.asBool(available);
    }
    public boolean isPremium() {
        return DynadotConvertUtils.asBool(premium);
    }

    public Optional<DomainPriceEntry> getPriceByYearPeriod(int year){
        return priceList.stream().filter((price) -> price.getPeriod() == year).findFirst();
    }
}
