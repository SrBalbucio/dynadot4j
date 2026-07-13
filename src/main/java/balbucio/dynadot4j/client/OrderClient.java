package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class OrderClient extends Client {

    public OrderClient(Dynadot dynadot) {
        super(dynadot);
    }

    public Future<Void> cancelTransfer(String orderId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        requester.post(getPath(orderId + "/cancel_transfer"), "")
                .whenComplete((response, ex) -> {
                    if (ex != null) {
                        future.completeExceptionally(ex);
                    } else {
                        future.complete(null);
                    }
                });
        return future;
    }

    public Future<Void> setTransferAuthCode(String orderId, String authCode) {
        JSONObject body = new JSONObject();
        body.put("auth_code", authCode);

        CompletableFuture<Void> future = new CompletableFuture<>();
        requester.post(getPath(orderId + "/transfer_auth_code"), body.toString())
                .whenComplete((response, ex) -> {
                    if (ex != null) {
                        future.completeExceptionally(ex);
                    } else {
                        future.complete(null);
                    }
                });
        return future;
    }

    private String getPath(String additional) {
        return "restful/v2/orders" + (additional != null ? "/" + additional : "");
    }
}
