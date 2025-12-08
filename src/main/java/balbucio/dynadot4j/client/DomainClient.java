package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.action.DomainRegistration;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.DomainRegisterResult;
import balbucio.dynadot4j.model.DomainSearchResult;
import balbucio.dynadot4j.model.DynadotHttpResponse;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DomainClient extends Client {

    public DomainClient(Dynadot dynadot) {
        super(dynadot);
    }

    public Future<DomainSearchResult> search(@NonNull String domainName, @Nullable String currency) {
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);
        if (currency == null) currency = "USD";

        CompletableFuture<DynadotHttpResponse> future =
                requester.get(getPath(domainName + "/search?show_price=true&currency=" + currency.toUpperCase()));

        return future.thenApply((response) -> response.asClazz(gson, DomainSearchResult.class));
    }

    public Future<ArrayList<String>> getSuggestionSearch(@NonNull String domainName, @NonNull List<String> tlds) {
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);
        if (tlds.isEmpty()) tlds.add("com");

        return requester.get(getPath(domainName + "/suggestion_search?tlds=" + String.join(",", tlds)))
                .thenApply((response) ->
                        response.asJSON().getJSONArray("domain_list")
                                .toList().stream().map((obj) -> (String) obj)
                                .collect(Collectors.toCollection(ArrayList::new)))
                .exceptionally((ex) -> new ArrayList<>());
    }

    public Future<DomainRegisterResult> register(DomainRegistration action) {

        return requester.post(getPath(action.getDomainName() + "/register"), action.toJSON().toString())
                .thenApply((response) -> response.asClazz(gson, DomainRegisterResult.class));
    }

    private String getPath(String additional) {
        return "restful/v1/domains" + (additional != null ? "/" + additional : "");
    }
}
