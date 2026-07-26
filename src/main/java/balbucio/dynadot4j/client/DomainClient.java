package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.action.DomainRegistration;
import balbucio.dynadot4j.action.DomainTransfer;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.*;
import com.google.gson.JsonSyntaxException;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DomainClient extends Client {

    public DomainClient(Dynadot dynadot) {
        super(dynadot);
    }

    /**
     * Pesquisa a disponibilidade de um domínio e recupera detalhes para registro.
     *
     * @param domainName domínio interessado
     * @param currency   moeda em que os valores devem ser retornados (USD, BRL)
     * @return resultado da pesquisa numa promessa
     */
    public Future<DomainSearchResult> search(@NonNull String domainName, @Nullable String currency) {
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);
        if (currency == null) currency = "USD";

        CompletableFuture<DynadotHttpResponse> future =
                requester.get(getPath(domainName + "/search?show_price=true&currency=" + currency.toUpperCase()));

        return future.thenApply((response) -> response.asClazz(gson, DomainSearchResult.class));
    }

    public Future<List<BulkSearchResult>> searchBulk(@NonNull String domainName, @Nullable String currency) {
        // TODO criar lista de possíveis domínios de forma menos arcaica
        String word = domainName.split("\\.")[0]; // remove o nome antigo
        return searchBulk(Arrays.asList(
                word + ".net",
                word + ".com",
                word + ".xyz",
                word + ".co",
                word + ".vip",
                word + ".bio",
                word + ".app",
                word + ".dev",
                word + ".org",
                word + ".shop",
                word + ".store",
                word + ".site",
                word + ".wiki",
                word + ".host"
        ), currency);
    }

    /**
     * Pesquisa a disponibilidade de vários domínios e recupera detalhes para registro.
     *
     * @param domainNames domínio interessado
     * @param currency    moeda em que os valores devem ser retornados (USD, BRL)
     * @return resultado da pesquisa numa promessa
     */
    public Future<List<BulkSearchResult>> searchBulk(@NonNull List<String> domainNames, @Nullable String currency) {
        if (domainNames.isEmpty())
            throw new InvalidDomainException(domainNames);
        if (currency == null)
            currency = "USD";
        if (domainNames.size() > config.getPriceLevel().getSearchLimit())
            domainNames = domainNames.subList(0, config.getPriceLevel().getSearchLimit() - 1);

        CompletableFuture<DynadotHttpResponse> future =
                requester.get(getPath("bulk_search?show_price=true&currency=" + currency.toUpperCase() + "&domain_name_list=" + String.join(",", domainNames)));

        return future.thenApply((response) -> response.asJSON()
                .getJSONArray("domain_result_list").toList().stream()
                .map((obj) -> {
                    try {
                        return gson.fromJson(new JSONObject((Map<String, Object>) obj).toString(), BulkSearchResult.class);
                    } catch (JsonSyntaxException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Procura por sugestões para um domínio (nem sempre tem)
     *
     * @param domainName domínio para registro
     * @param tlds       extensões de domínios desejadas (ex.: com, net, xyz)
     * @return lista de sugestões de domínio numa promessa
     */
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

    /**
     * Inicia o processo de registro de um domínio (confirme que você tem saldo disponível).
     *
     * @param action ação de registro de domínio
     * @return resultado do registro numa promessa
     */
    public Future<DomainRegisterResult> register(DomainRegistration action) {
        return requester.post(getPath(action.getDomainName() + "/register"), action.toJSON().toString())
                .thenApply((response) -> response.asClazz(gson, DomainRegisterResult.class));
    }

    /**
     * Renova o domínio fornecido (confirme que você tem saldo disponível)
     *
     * @param domainName                        domínio para renovação
     * @param duration                          tempo de renovação (1-10 anos)
     * @param year                              ano de efetivação da renovação (geralmente o ano da expiração)
     * @param no_renew_if_late_renew_fee_needed não renove se for necessária uma taxa de renovação tardia (por padrão, false).
     * @return a nova data de expiração do domínio
     */
    public Future<Long> renew(@NonNull String domainName, int duration, int year, boolean no_renew_if_late_renew_fee_needed) {
        JSONObject body = new JSONObject()
                .put("duration", duration)
                .put("year", year)
                .put("no_renew_if_late_renew_fee_needed", no_renew_if_late_renew_fee_needed);

        return requester.post(getPath(domainName + "/renew"), body.toString())
                .thenApply((response) -> response.asJSON().getLong("expiration_date"));
    }

    /**
     * Renova o domínio fornecido (confirme que você tem saldo disponível)
     *
     * @param domainName domínio para renovação
     * @param duration   tempo de renovação (1-10 anos)
     * @param year       ano de efetivação da renovação (geralmente o ano da expiração)
     * @return a nova data de expiração do domínio numa promessa
     */
    public Future<Long> renew(@NonNull String domainName, int duration, int year) {
        return renew(domainName, duration, year, false);
    }

    public Future<Long> renew(@NonNull String domainName, int duration, Date expirationDate) {
        return renew(domainName, duration, expirationDate.getYear());
    }

    public Future<Long> renew(@NonNull String domainName, int duration, LocalDate expirationDate) {
        return renew(domainName, duration, expirationDate.getYear());
    }

    public Future<Long> renew(@NonNull String domainName, int duration, LocalDateTime expirationDate) {
        return renew(domainName, duration, expirationDate.getYear());
    }

    public Future<Long> renew(@NonNull String domainName, int duration, DomainInfo domainInfo) {
        return renew(domainName, duration, domainInfo.getExpirationDate());
    }

    public Future<Long> renew(@NonNull String domainName, int duration, DomainRegisterResult fromResult) {
        return renew(domainName, duration, fromResult.getExpirationDate());
    }

    /**
     * Defina os nameservers do domínio
     *
     * @param domainName  domínio a ser alterado
     * @param nameservers lista de nameserver (ex.: ns01.example.com)
     * @return promessa de conclusão
     */
    public Future<Void> setNameservers(String domainName, List<String> nameservers) {
        JSONObject body = new JSONObject();
        body.put("nameserver_list", nameservers);
        return requester.put(getPath(domainName + "/nameservers"), body.toString())
                .thenApply((response) -> null);
    }

    /**
     * Define o domínio como estacionado (sem uso)
     *
     * @param domainName domínio a ser estacionado
     * @param ads        incluir AD's de terceiros
     * @return promessa de conclusão
     */
    public Future<Void> setParking(String domainName, boolean ads) {
        JSONObject body = new JSONObject();
        body.put("with_ads", ads);

        return requester.put(getPath(domainName + "/parking"), body.toString())
                .thenApply((response) -> null);
    }

    /**
     * Define o nível de privacidade de um domínio.
     *
     * @param domainName   domínio a ser alterado
     * @param level        nível de proteção (FULL, PARTIAL)
     * @param whoIsPrivacy proteger o domínio no WHOIS
     * @return promessa de conclusão
     */
    public Future<Void> setPrivacy(String domainName, DomainPrivacy level, boolean whoIsPrivacy) {
        JSONObject body = new JSONObject();
        body.put("privacy_level", level.getLabel());
        body.put("whois_privacy_option", whoIsPrivacy);

        return requester.put(getPath(domainName + "/privacy"), body.toString())
                .thenApply((response) -> null);
    }

    /**
     * Definir redirecionamento automático ao domínio
     *
     * @param domainName domínio a ser alterado
     * @param forwardUrl URL de redirecionamento
     * @param temporary  informa ao cliente se o redirecionamento é temporário ou permanente
     * @return promessa de conclusão
     */
    public Future<Void> setForwarding(String domainName, String forwardUrl, boolean temporary) {
        JSONObject body = new JSONObject();
        body.put("forward_url", forwardUrl);
        body.put("is_temporary", temporary);

        return requester.put(getPath(domainName + "/domain_forwarding"), body.toString())
                .thenApply((response) -> null);
    }

    /**
     * Define os registros DNSSEC
     *
     * @param domainName domínio a ser alterado
     * @param algorithm  algoritmo
     * @param digest     digest
     * @param digestType tipo do digest
     * @param keyTag     key tag
     * @param publicKey  public key
     * @return promessa de conclusão
     */
    public Future<Void> setDNSSEC(
            String domainName,
            DNSSECAlgorithm algorithm,
            String digest,
            DigestType digestType,
            int keyTag,
            String publicKey
    ) {
        JSONObject body = new JSONObject();
        body.put("digest", digest);
        body.put("digest_type", digestType.getLabel());
        body.put("key_tag", keyTag);
        body.put("algorithm", algorithm.getLabel());
        body.put("public_key", publicKey);

        return requester.put(getPath(domainName + "/dnssec"), body.toString())
                .thenApply((response) -> null);
    }

    public Future<Void> clearDNSSEC(String domainName) {
        return requester.del(getPath(domainName + "/dnssec"))
                .thenApply((response) -> null);
    }


    /**
     * Defina a operação desejada na renovação (RESET, AUTO, DONOT)
     *
     * @param domainName domínio a ser alterado
     * @param option     opção desejada para renovação
     * @return promessa de conclusão
     */
    public Future<Void> setRenewOption(String domainName, DomainRenewOption option) {
        JSONObject body = new JSONObject();
        body.put("renew_option", option.getLabel());

        return requester.put(getPath(domainName + "/renew_option"), body.toString())
                .thenApply((response) -> null);
    }

    /**
     * Recupera os detalhes de domínio registrado.
     *
     * @param domainName domínio a ser exibido
     * @return detalhes do domínio numa promessa
     */
    public Future<DomainInfo> getDomain(String domainName) {
        return requester.get(getPath(domainName))
                .thenApply((response) -> {
                    DomainInfoResponse domainInfoResponse = response.asClazz(gson, DomainInfoResponse.class);
                    return domainInfoResponse.getDomainInfo();
                });
    }

    public Future<DomainTransferResult> transferIn(DomainTransfer action) {
        return requester.post(getPath(action.getDomainName() + "/transfer_in"), action.toJSON().toString())
                .thenApply(response -> response.asClazz(gson, DomainTransferResult.class));
    }

    public Future<TransferStatusResponse> getTransferStatus(String domainName, String transferType) {
        return requester.get(getPath(domainName + "/transfer_status?transfer_type=" + transferType))
                .thenApply(response -> response.asClazz(gson, TransferStatusResponse.class));
    }

    public Future<String> getTransferAuthCode(String domainName) {
        return requester.get(getPath(domainName + "/transfer_auth_code"))
                .thenApply(response -> response.asJSON().getString("auth_code"));
    }

    public Future<Void> push(String domainName, String receiverPushUsername) {
        JSONObject body = new JSONObject();
        body.put("receiver_push_username", receiverPushUsername);
        return requester.post(getPath(domainName + "/push"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> push(String domainName, String receiverPushUsername, boolean unlockDomainForPush) {
        JSONObject body = new JSONObject();
        body.put("receiver_push_username", receiverPushUsername);
        body.put("unlock_domain_for_push", unlockDomainForPush);
        return requester.post(getPath(domainName + "/push"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> push(String domainName, String receiverPushUsername, boolean unlockDomainForPush, String currency) {
        JSONObject body = new JSONObject();
        body.put("receiver_push_username", receiverPushUsername);
        body.put("unlock_domain_for_push", unlockDomainForPush);
        body.put("currency", currency);
        return requester.post(getPath(domainName + "/push"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> acceptPush(String domainName) {
        JSONObject body = new JSONObject();
        body.put("domain_name", domainName);
        body.put("action", "accept");
        return requester.post(getPath("push/accept"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> declinePush(String domainName) {
        JSONObject body = new JSONObject();
        body.put("domain_name", domainName);
        body.put("action", "decline");
        return requester.post(getPath("push/accept"), body.toString())
                .thenApply(response -> null);
    }

    public Future<List<String>> getPendingPushRequests() {
        return requester.get(getPath("push/pending"))
                .thenApply(response ->
                        response.asJSON().getJSONArray("push_domain_name")
                                .toList().stream()
                                .map(obj -> (String) obj)
                                .collect(Collectors.toCollection(ArrayList::new)));
    }

    public Future<Void> authorizeTransferAway(String domainName, String orderId, boolean approve) {
        JSONObject body = new JSONObject();
        body.put("order_id", orderId);
        body.put("authorize", approve ? "approve" : "deny");
        return requester.post(getPath(domainName + "/authorize_transfer_away"), body.toString())
                .thenApply(response -> null);
    }

    public Future<ResellerHoldStatus> getResellerHoldStatus(String domainName) {
        return requester.get(getPath(domainName + "/reseller/hold/status"))
                .thenApply(response -> {
                    ResellerHoldStatusResponse r = response.asClazz(gson, ResellerHoldStatusResponse.class);
                    return r.getStatus();
                });
    }

    public Future<Void> setResellerHoldStatus(String domainName, ResellerHoldStatus status) {
        JSONObject body = new JSONObject();
        body.put("hold", status.getLabel());
        return requester.put(getPath(domainName + "/reseller/hold/status"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Long> getResellerCustomerId(String domainName) {
        return requester.get(getPath(domainName + "/reseller/customer-id"))
                .thenApply(response -> {
                    ResellerCustomerIdResponse r = response.asClazz(gson, ResellerCustomerIdResponse.class);
                    return r.getCustomerId();
                });
    }

    public Future<Void> setResellerCustomerId(String domainName, long customerId) {
        JSONObject body = new JSONObject();
        body.put("customer_id", customerId);
        return requester.put(getPath(domainName + "/reseller/customer-id"), body.toString())
                .thenApply(response -> null);
    }

    public Future<List<DnsssecRecord>> getDnssec(String domainName) {
        return requester.get(getPath(domainName + "/dnssec"))
                .thenApply(response -> {
                    DnsssecRecordResponse r = response.asClazz(gson, DnsssecRecordResponse.class);
                    return r.getDnssecList();
                });
    }

    public Future<List<NameServerInfo>> getNameservers(String domainName) {
        return requester.get(getPath(domainName + "/nameservers"))
                .thenApply(response -> {
                    NameServerListResponse r = response.asClazz(gson, NameServerListResponse.class);
                    return r.getNameServers();
                });
    }

    public Future<List<BulkSearchResult>> powerSearch(String domainName, @Nullable Boolean showPrice, @Nullable String currency) {
        StringBuilder query = new StringBuilder(domainName + "/power_search_new");
        List<String> params = new ArrayList<>();
        if (showPrice != null) params.add("show_price=" + showPrice);
        if (currency != null) params.add("currency=" + currency.toUpperCase());
        if (!params.isEmpty()) query.append("?").append(String.join("&", params));
        return requester.get(getPath(query.toString()))
                .thenApply(response -> response.asJSON()
                        .getJSONArray("domain_result_list").toList().stream()
                        .map(obj -> {
                            try {
                                return gson.fromJson(new JSONObject((Map<String, Object>) obj).toString(), BulkSearchResult.class);
                            } catch (JsonSyntaxException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(ArrayList::new)));
    }

    public Future<List<DomainInfo>> getDomainList(int page, int pageSize) {
        return requester.get(getPath("?page=" + page + "&page_size=" + pageSize))
                .thenApply(response -> {
                    JSONObject data = response.asJSON();
                    if (!data.has("domain_list")) return new ArrayList<>();
                    return data.getJSONArray("domain_list").toList().stream()
                            .map(obj -> {
                                try {
                                    return gson.fromJson(new JSONObject((Map<String, Object>) obj).toString(), DomainInfo.class);
                                } catch (JsonSyntaxException e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toCollection(ArrayList::new));
                });
    }

    public Future<Long> restore(String domainName, @Nullable String currency, @Nullable String couponCode) {
        JSONObject body = new JSONObject();
        if (currency != null) body.put("currency", currency);
        if (couponCode != null) body.put("coupon_code", couponCode);
        return requester.post(getPath(domainName + "/restore"), body.toString())
                .thenApply(response -> response.asClazz(gson, DomainRestoreResult.class).getOrderId());
    }

    public Future<Long> restore(String domainName) {
        return restore(domainName, null, null);
    }

    public Future<String> getDomainAppraisal(String domainName) {
        return requester.get(getPath(domainName + "/appraisal"))
                .thenApply(response -> response.asClazz(gson, DomainAppraisalResponse.class).getAppraisalPrice());
    }

    /**
     * Recupera os preços de TLDs (Top-Level Domains).
     *
     * @param tld         extensão de domínio desejada (ex.: com, net)
     * @param currency    moeda em que os valores devem ser retornados (USD, BRL)
     * @param priceLevel  nível de preço da conta (Regular, Bulk, Super Bulk)
     * @param sort        campo para ordenação
     * @param page        página dos resultados
     * @param pageSize    quantidade de resultados por página
     * @return lista de preços de TLDs numa promessa
     */
    public Future<List<TldPriceEntry>> getTldPrice(@Nullable String tld, @Nullable String currency, @Nullable String priceLevel,
                                            @Nullable String sort, int page, int pageSize) {
        List<String> params = new ArrayList<>();
        params.add("page=" + page);
        params.add("page_size=" + pageSize);
        if (tld != null) params.add("tld=" + tld);
        if (currency != null) params.add("currency=" + currency);
        if (priceLevel != null) params.add("price_level=" + priceLevel);
        if (sort != null) params.add("sort=" + sort);
        return requester.get(getPath("get_tld_price?" + String.join("&", params)))
                .thenApply(response -> response.asJSON()
                        .getJSONArray("tld_price_list").toList().stream()
                        .map(obj -> {
                            try {
                                return gson.fromJson(new JSONObject((Map<String, Object>) obj).toString(), TldPriceEntry.class);
                            } catch (JsonSyntaxException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(ArrayList::new)));
    }

    public Future<Void> graceDelete(String domainName, boolean addToWaitingList) {
        return requester.del(getPath(domainName + "/grace_delete?add_to_waiting_list=" + addToWaitingList))
                .thenApply(response -> null);
    }

    public Future<Void> graceDelete(String domainName) {
        return graceDelete(domainName, false);
    }

    public Future<Void> postGraceDelete(String domainName) {
        return requester.del(getPath(domainName + "/post_grace_delete"))
                .thenApply(response -> null);
    }

    public Future<Void> setFolder(String domainName, String folderName) {
        return requester.put(getPath(domainName + "/folders/" + folderName), "{}")
                .thenApply(response -> null);
    }

    public Future<Void> setStealthForwarding(String domainName, String stealthUrl, String stealthTitle) {
        JSONObject body = new JSONObject();
        body.put("stealth_url", stealthUrl);
        body.put("stealth_title", stealthTitle);
        return requester.put(getPath(domainName + "/stealth_forwarding"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> setEmailForwarding(String domainName, String emailForwardType,
                                            @Nullable List<JSONObject> emailAliasList,
                                            @Nullable List<JSONObject> emailExchangeList) {
        JSONObject body = new JSONObject();
        body.put("email_forward_type", emailForwardType);
        if (emailAliasList != null) body.put("email_alias_list", emailAliasList);
        if (emailExchangeList != null) body.put("email_exchange_list", emailExchangeList);
        return requester.put(getPath(domainName + "/email_forwarding"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> setContacts(String domainName, int registrantContactId, int adminContactId,
                                     int techContactId, int billingContactId) {
        JSONObject body = new JSONObject();
        body.put("registrant_contact_id", registrantContactId);
        body.put("admin_contact_id", adminContactId);
        body.put("technical_contact_id", techContactId);
        body.put("billing_contact_id", billingContactId);
        return requester.put(getPath(domainName + "/contacts"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> setHosting(String domainName, String hostingType, boolean isModelView) {
        JSONObject body = new JSONObject();
        body.put("hosting_type", hostingType);
        body.put("is_model_view", isModelView);
        return requester.put(getPath(domainName + "/hosts"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> clearDomainSetting(String domainName, String serviceType) {
        JSONObject body = new JSONObject();
        body.put("service_type", serviceType);
        return requester.put(getPath(domainName + "/clear_domain_setting"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> setDomainLockStatus(String domainName, boolean lock) {
        JSONObject body = new JSONObject();
        body.put("lock", lock);
        return requester.put(getPath(domainName + "/domain_lock"), body.toString())
                .thenApply(response -> null);
    }

    public Future<List<String>> getPendingPushAcceptRequests() {
        return requester.get(getPath("pending_accept_pushes"))
                .thenApply(response ->
                        response.asJSON().getJSONArray("push_domain_name")
                                .toList().stream()
                                .map(obj -> (String) obj)
                                .collect(Collectors.toCollection(ArrayList::new)));
    }

    private String getPath(String additional) {
        return "restful/v2/domains" + (additional != null ? "/" + additional : "");
    }
}
