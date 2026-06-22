package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.model.RegistrantContact;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Getter
public class ContactClient extends Client {

    public ContactClient(Dynadot dynadot) {
        super(dynadot);
    }

    public Future<Integer> createContact(RegistrantContact contact) {
        return requester.post(getPath(null), gson.toJson(contact))
                .thenApply((response) -> response.asJSON().getInt("contact_id"));
    }

    public Future<RegistrantContact> getContact(int contactId) {
        return requester.get(getPath(String.valueOf(contactId)))
                .thenApply((response) -> response.asClazz(gson, RegistrantContact.class));
    }

    public Future<Integer> updateContact(int contactId, RegistrantContact contact) {
        return requester.post(getPath(String.valueOf(contactId)), gson.toJson(contact))
                .thenApply((response) -> response.asJSON().getInt("contact_id"));
    }

    public CompletableFuture<Void> deleteContact(int contactId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        requester.del(String.valueOf(contactId)).whenComplete((ignored, ex) -> {
            if (ex != null) {
                future.completeExceptionally(ex);
            }
            future.complete(null);
        });
        return future;
    }

    private String getPath(String additional) {
        return "restful/v2/contacts" + (additional != null ? "/" + additional : "");
    }
}
