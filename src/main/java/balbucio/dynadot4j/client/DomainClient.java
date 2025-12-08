package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.DomainSearchResult;
import balbucio.dynadot4j.model.DynadotHttpResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class DomainClient extends Client {

    public DomainClient(Dynadot dynadot) {
        super(dynadot);
    }

    public Future<DomainSearchResult> search(String domainName, String currency) {
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);

        CompletableFuture<DomainSearchResult> result = new CompletableFuture<>();
        CompletableFuture<DynadotHttpResponse> future =
                requester.get(getPath(domainName + "/search?show_price=true&currency=" + currency.toUpperCase()));

        future.handle((response, ex) -> {
            if (ex != null) return result.completeExceptionally(ex);
            return result.complete(response.asClazz(gson, DomainSearchResult.class));
        });

        return result;
    }

    private String getPath(String additional) {
        return "restful/v1/domains" + (additional != null ? "/"+additional : "");
    }
}
