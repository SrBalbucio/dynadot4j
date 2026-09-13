package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.model.Contact;
import balbucio.dynadot4j.model.ContactListResponse;
import balbucio.dynadot4j.model.RegistrantContact;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Getter
public class ContactClient extends Client {

    public ContactClient(Dynadot dynadot) {
        super(dynadot);
    }

    /**
     * POST /restful/v2/contacts — body {@code {"contact": {...}}}.
     * Campos obrigatórios: name, email, phone_number, phone_cc, address1, city, zip, country.
     */
    public Future<Integer> createContact(RegistrantContact contact) {
        JSONObject body = new JSONObject();
        body.put("contact", contact.toJSON());
        return requester.post(getPath(null), body.toString())
                .thenApply((response) -> response.asJSON().getInt("contact_id"));
    }

    /**
     * GET /restful/v2/contacts/{contact_id} — resposta em {@code data.contact}.
     */
    public Future<RegistrantContact> getContact(int contactId) {
        return requester.get(getPath(String.valueOf(contactId)))
                .thenApply((response) -> {
                    JSONObject data = response.asJSON();
                    JSONObject contactJson = data.has("contact")
                            ? data.getJSONObject("contact")
                            : data;
                    return gson.fromJson(contactJson.toString(), RegistrantContact.class);
                });
    }

    /**
     * GET /restful/v2/contacts — lista com filtros opcionais e paginação.
     * Resposta em {@code data.contact_list}.
     */
    public Future<List<Contact>> listContacts(@Nullable String whoisVerificationStatus,
                                              @Nullable Boolean inUse,
                                              @Nullable String cnnicCnAuditStatus,
                                              int page,
                                              int pageSize) {
        List<String> params = new ArrayList<>();
        if (whoisVerificationStatus != null) {
            params.add("whois_verification_status=" + encode(whoisVerificationStatus));
        }
        if (inUse != null) params.add("in_use=" + inUse);
        if (cnnicCnAuditStatus != null) params.add("cnnic_cn_audit_status=" + encode(cnnicCnAuditStatus));
        if (page > 0) params.add("page=" + page);
        if (pageSize > 0) params.add("page_size=" + pageSize);

        String path = getPath(null) + (params.isEmpty() ? "" : "?" + String.join("&", params));
        return requester.get(path)
                .thenApply((response) -> {
                    ContactListResponse list = response.asClazz(gson, ContactListResponse.class);
                    return list.getContactList() != null ? list.getContactList() : new ArrayList<>();
                });
    }

    public Future<List<Contact>> listContacts(int page, int pageSize) {
        return listContacts(null, null, null, page, pageSize);
    }

    public Future<List<Contact>> listContacts() {
        return listContacts(null, null, null, 0, 0);
    }

    /**
     * PUT /restful/v2/contacts/{contact_id} — body {@code {"contact": {...}}}.
     */
    public Future<Integer> updateContact(int contactId, RegistrantContact contact) {
        JSONObject body = new JSONObject();
        body.put("contact", contact.toJSON());
        return requester.put(getPath(String.valueOf(contactId)), body.toString())
                .thenApply((response) -> response.asJSON().getInt("contact_id"));
    }

    /**
     * DELETE /restful/v2/contacts/{contact_id}.
     */
    public CompletableFuture<Void> deleteContact(int contactId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        requester.del(getPath(String.valueOf(contactId))).whenComplete((ignored, ex) -> {
            if (ex != null) {
                future.completeExceptionally(ex);
                return;
            }
            future.complete(null);
        });
        return future;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String getPath(String additional) {
        return "restful/v2/contacts" + (additional != null ? "/" + additional : "");
    }
}
