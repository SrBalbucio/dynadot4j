package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.AuctionDetailsResponse;
import balbucio.dynadot4j.model.AuctionItemDetails;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Cliente da API de aftermarket (leilões) da Dynadot.
 *
 * <p>Endpoints cobertos (RESTful v2):
 * <ul>
 *     <li>{@code get_auction_details} -> {@code GET /restful/v2/aftermarket/auctions/{domain_name}?currency=USD}</li>
 *     <li>{@code place_auction_bid} -> {@code POST /restful/v2/aftermarket/auctions/bids/{domain_name}}</li>
 * </ul>
 */
public class AftermarketClient extends Client {

    public AftermarketClient(Dynadot dynadot) {
        super(dynadot);
    }

    /**
     * Recupera os detalhes de um leilão.
     *
     * @param domainName domínio do leilão
     * @param currency   moeda (ex.: USD, BRL). Se nulo, usa USD.
     * @return detalhes do leilão numa promessa
     */
    public Future<AuctionItemDetails> getAuctionDetails(@NonNull String domainName, @Nullable String currency) {
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);
        if (currency == null) currency = "USD";

        return requester.get(getPath("auctions/" + domainName + "?currency=" + currency.toUpperCase()))
                .thenApply(response -> response.asClazz(gson, AuctionDetailsResponse.class).getAuctionItemDetails());
    }

    public Future<AuctionItemDetails> getAuctionDetails(@NonNull String domainName) {
        return getAuctionDetails(domainName, "USD");
    }

    /**
     * Dá um lance num leilão.
     *
     * @param domainName domínio do leilão
     * @param bidAmount  valor do lance
     * @param currency   moeda (ex.: USD). Se nulo, usa USD.
     * @param isBackorderAuction se é leilão de backorder (opcional)
     * @return detalhes atualizados do leilão numa promessa
     */
    public Future<AuctionItemDetails> placeAuctionBid(@NonNull String domainName, double bidAmount,
                                                      @Nullable String currency,
                                                      @Nullable Boolean isBackorderAuction) {
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);
        if (currency == null) currency = "USD";

        JSONObject body = new JSONObject()
                .put("bid_amount", bidAmount)
                .put("currency", currency.toUpperCase());
        if (isBackorderAuction != null) body.put("is_backorder_auction", isBackorderAuction);

        return requester.post(getPath("auctions/bids/" + domainName), body.toString())
                .thenApply(response -> response.asClazz(gson, AuctionDetailsResponse.class).getAuctionItemDetails());
    }

    public Future<AuctionItemDetails> placeAuctionBid(@NonNull String domainName, double bidAmount,
                                                      @Nullable String currency) {
        return placeAuctionBid(domainName, bidAmount, currency, null);
    }

    private String getPath(String additional) {
        return "restful/v2/aftermarket" + (additional != null ? "/" + additional : "");
    }
}
