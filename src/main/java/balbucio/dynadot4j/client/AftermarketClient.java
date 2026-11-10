package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.AuctionDetailsResponse;
import balbucio.dynadot4j.model.AuctionItemDetails;
import balbucio.dynadot4j.model.ListingListResponse;
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
 *     <li>{@code get_listings} -> {@code GET /restful/v2/aftermarket/listings}</li>
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

    /**
     * Lista os anúncios públicos do marketplace da Dynadot.
     *
     * <p>Contrato: {@code GET /restful/v2/aftermarket/listings} com
     * {@code currency}, {@code page} e {@code page_size} obrigatórios
     * ({@code page} começa em 1 e {@code page_size} aceita até 20000).
     *
     * @param currency            moeda dos preços (ex.: USD, BRL)
     * @param page                índice da página, começando em 1
     * @param pageSize            itens por página (1 a 20000)
     * @param excludePendingSale  excluir anúncios com venda pendente (opcional)
     * @param showOtherRegistrar  incluir anúncios de outros registradores (opcional)
     * @return página de anúncios numa promessa
     */
    public Future<ListingListResponse> getListings(@NonNull String currency, int page, int pageSize,
                                                   @Nullable Boolean excludePendingSale,
                                                   @Nullable Boolean showOtherRegistrar) {
        if (currency.isEmpty()) throw new IllegalArgumentException("currency is required.");
        if (page < 1) throw new IllegalArgumentException("page must start at 1.");
        if (pageSize < 1 || pageSize > 20000) {
            throw new IllegalArgumentException("page_size must be between 1 and 20000.");
        }

        List<String> params = new ArrayList<>();
        params.add("currency=" + currency.toUpperCase());
        params.add("page=" + page);
        params.add("page_size=" + pageSize);
        if (excludePendingSale != null) params.add("exclude_pending_sale=" + excludePendingSale);
        if (showOtherRegistrar != null) params.add("show_other_registrar=" + showOtherRegistrar);

        return requester.get(getPath("listings") + "?" + String.join("&", params))
                .thenApply(response -> response.asClazz(gson, ListingListResponse.class));
    }

    public Future<ListingListResponse> getListings(@NonNull String currency, int page, int pageSize) {
        return getListings(currency, page, pageSize, null, null);
    }

    private String getPath(String additional) {
        return "restful/v2/aftermarket" + (additional != null ? "/" + additional : "");
    }
}
