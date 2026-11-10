package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Envelope devolvido por {@code get_listings} ({@code GET /restful/v2/aftermarket/listings}).
 *
 * <p>O corpo chega em {@code data} com {@code total} e {@code listing_item_list}.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ListingListResponse {

    /**
     * Total de anúncios que casam com o filtro, útil para paginar.
     * A documentação declara {@code Integer}, mas o exemplo usa um valor maior
     * que cabe em {@code Long}, por isso o tipo largo.
     */
    @SerializedName("total")
    private Long total;

    @SerializedName("listing_item_list")
    private List<ListingItem> listingItemList;

    public List<ListingItem> getItems() {
        return listingItemList != null ? listingItemList : new ArrayList<>();
    }
}
