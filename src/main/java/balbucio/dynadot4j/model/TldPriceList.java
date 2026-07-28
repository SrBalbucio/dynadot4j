package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TldPriceList {

    private int page;
    @SerializedName("page_size")
    private int pageSize;
    private String sort;
    @SerializedName("price_level")
    private String priceLevel;
    private String currency;
    @SerializedName("show_multi_year_price")
    private boolean showMultiYearPrice;
    @SerializedName("tld_price_list")
    private List<TldPriceEntry> priceList;
}
