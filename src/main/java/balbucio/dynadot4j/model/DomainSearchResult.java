package balbucio.dynadot4j.model;

import balbucio.dynadot4j.utils.DynadotModelUtils;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode
public class DomainSearchResult {

    @SerializedName("domain_name")
    private String domainName;
    private String available;
    private String premium;
    @SerializedName("show_price")
    private String showPrice;
    @SerializedName("price_list")
    private List<DomainPriceEntry> priceList;

    public boolean isAvailable() {
        System.out.println(available);
        return DynadotModelUtils.asBool(available);
    }

    public boolean isPremium() {
        return DynadotModelUtils.asBool(premium);
    }

    public boolean isShowingPrice() {
        return DynadotModelUtils.asBool(showPrice);
    }

    public Optional<DomainPriceEntry> getPriceByYearPeriod(int year){
        return priceList.stream().filter((price) -> price.getPeriod() == year).findFirst();
    }

}
