package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.DomainSearchResult;
import balbucio.dynadot4j.model.DynadotHttpResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DomainClient extends Client {

    public DomainClient(Dynadot dynadot) {
        super(dynadot);
    }

    public Future<DomainSearchResult> search(String domainName, String currency) {
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);

        CompletableFuture<DynadotHttpResponse> future =
                requester.get(getPath(domainName + "/search?show_price=true&currency=" + currency.toUpperCase()));

        return future.thenApply((response) -> response.asClazz(gson, DomainSearchResult.class));
    }

    public Future<List<String>> getSuggestionSearch(String domainName) {
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);

        return requester.get(getPath(domainName + "/suggestion_search"))
                .thenApply((response) ->
                        response.asJSON().getJSONArray("domain_list")
                                .toList().stream().map((obj) -> (String) obj)
                                .collect(Collectors.toCollection(ArrayList::new)));
    }

    private String getPath(String additional) {
        return "restful/v1/domains" + (additional != null ? "/" + additional : "");
    }
}
