package balbucio.dynadot4j.model;

import balbucio.dynadot4j.utils.DynadotConvertUtils;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Item de anúncio do marketplace público da Dynadot, retornado por {@code get_listings}
 * ({@code GET /restful/v2/aftermarket/listings}).
 *
 * <p>Os campos {@code registration_date} e {@code expiration_date} chegam em milissegundos
 * desde a época e são expostos como {@link Date}; o valor bruto não é perdido porque os
 * getters de data só existem quando o campo é maior que zero.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ListingItem {

    @SerializedName("listing_id")
    private Integer listingId;
    private String domain;
    private String price;
    @SerializedName("in_bound_links")
    private Integer inBoundLinks;
    private Integer age;
    @SerializedName("is_pending_sale_locked")
    private String isPendingSaleLocked;
    private String type;
    @SerializedName("registration_date")
    private Long registrationDateMillis;
    @SerializedName("expiration_date")
    private Long expirationDateMillis;

    /**
     * A API documenta {@code is_pending_sale_locked} como booleano, mas os exemplos
     * retornam as strings {@code "Yes"}/{@code "No"}. Converte os dois formatos.
     */
    public boolean isPendingSaleLocked() {
        return DynadotConvertUtils.asBool(isPendingSaleLocked);
    }

    public Date getRegistrationDate() {
        return registrationDateMillis != null && registrationDateMillis > 0
                ? new Date(registrationDateMillis) : null;
    }

    public Date getExpirationDate() {
        return expirationDateMillis != null && expirationDateMillis > 0
                ? new Date(expirationDateMillis) : null;
    }
}
