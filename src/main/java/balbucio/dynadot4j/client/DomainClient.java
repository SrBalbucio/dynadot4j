package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.action.DomainRegistration;
import balbucio.dynadot4j.action.DomainTransfer;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.*;
import com.google.gson.JsonSyntaxException;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

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
    /**
     * Contrato: GET /suggestion_search com tlds (req), max_count/show_price/currency (opt).
     * Resposta em {@code data.domain_list}: itens podem ser strings ou objetos com domain_name.
     */
    public Future<ArrayList<String>> getSuggestionSearch(@NonNull String domainName, @NonNull List<String> tlds) {
        return getSuggestionSearch(domainName, tlds, null, null, null);
    }

    public Future<ArrayList<String>> getSuggestionSearch(@NonNull String domainName, @NonNull List<String> tlds,
                                                         @Nullable Integer maxCount, @Nullable Boolean showPrice,
                                                         @Nullable String currency) {
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);
        if (tlds.isEmpty()) tlds.add("com");

        List<String> params = new ArrayList<>();
        params.add("tlds=" + String.join(",", tlds));
        if (maxCount != null) params.add("max_count=" + maxCount);
        if (showPrice != null) params.add("show_price=" + showPrice);
        if (currency != null) params.add("currency=" + currency.toUpperCase());

        return requester.get(getPath(domainName + "/suggestion_search?" + String.join("&", params)))
                .thenApply((response) ->
                        response.asJSON().getJSONArray("domain_list")
                                .toList().stream().map((obj) -> {
                                    if (obj instanceof String s) return s;
                                    if (obj instanceof Map<?, ?> map && map.containsKey("domain_name")) {
                                        Object name = map.get("domain_name");
                                        return name != null ? name.toString() : null;
                                    }
                                    return null;
                                })
                                .filter(Objects::nonNull)
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
     * Renova o domínio fornecido (confirme que você tem saldo disponível).
     * Contrato: POST /domains/{domain}/renew com duration (req), year (req),
     * currency (opt), coupon (opt), no_renew_if_late_renew_fee_needed (opt).
     */
    public Future<Long> renew(@NonNull String domainName, int duration, int year, boolean no_renew_if_late_renew_fee_needed) {
        return renew(domainName, duration, year, null, null, no_renew_if_late_renew_fee_needed);
    }

    public Future<Long> renew(@NonNull String domainName, int duration, int year,
                              @Nullable String currency, @Nullable String coupon,
                              boolean no_renew_if_late_renew_fee_needed) {
        JSONObject body = new JSONObject()
                .put("duration", duration)
                .put("year", year)
                .put("no_renew_if_late_renew_fee_needed", no_renew_if_late_renew_fee_needed);
        if (currency != null) body.put("currency", currency);
        if (coupon != null) body.put("coupon", coupon);

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
     * Contrato: PUT /domains/{domain}/privacy com privacy_level (req).
     */
    public Future<Void> setPrivacy(String domainName, DomainPrivacy level) {
        JSONObject body = new JSONObject();
        body.put("privacy_level", level.getLabel());

        return requester.put(getPath(domainName + "/privacy"), body.toString())
                .thenApply((response) -> null);
    }

    /**
     * @deprecated O contrato atual aceita apenas privacy_level; o parâmetro whoIsPrivacy é ignorado.
     */
    @Deprecated
    public Future<Void> setPrivacy(String domainName, DomainPrivacy level, boolean whoIsPrivacy) {
        return setPrivacy(domainName, level);
    }

    /**
     * Definir redirecionamento automático ao domínio.
     * Contrato: PUT /domain_forwarding com forward_url (req), is_temporary (opt),
     * enable_domain_variable (opt), enable_wildcard_forwarding (opt).
     */
    public Future<Void> setForwarding(String domainName, String forwardUrl, boolean temporary) {
        return setForwarding(domainName, forwardUrl, temporary, null, null);
    }

    public Future<Void> setForwarding(String domainName, String forwardUrl, boolean temporary,
                                       @Nullable Boolean enableDomainVariable,
                                       @Nullable Boolean enableWildcardForwarding) {
        JSONObject body = new JSONObject();
        body.put("forward_url", forwardUrl);
        body.put("is_temporary", temporary);
        if (enableDomainVariable != null) body.put("enable_domain_variable", enableDomainVariable);
        if (enableWildcardForwarding != null) body.put("enable_wildcard_forwarding", enableWildcardForwarding);

        return requester.put(getPath(domainName + "/domain_forwarding"), body.toString())
                .thenApply((response) -> null);
    }

    /**
     * Define os registros DNSSEC.
     * Contrato: PUT /dnssec com algorithm (req), key_tag/digest_type/digest/flags/public_key (opt).
     */
    public Future<Void> setDNSSEC(
            String domainName,
            DNSSECAlgorithm algorithm,
            String digest,
            DigestType digestType,
            int keyTag,
            String publicKey
    ) {
        return setDNSSEC(domainName, algorithm, digest, digestType, keyTag, publicKey, null);
    }

    public Future<Void> setDNSSEC(
            String domainName,
            DNSSECAlgorithm algorithm,
            @Nullable String digest,
            @Nullable DigestType digestType,
            @Nullable Integer keyTag,
            @Nullable String publicKey,
            @Nullable String flags
    ) {
        JSONObject body = new JSONObject();
        body.put("algorithm", algorithm.getLabel());
        if (digest != null) body.put("digest", digest);
        if (digestType != null) body.put("digest_type", digestType.getLabel());
        if (keyTag != null) body.put("key_tag", keyTag);
        if (publicKey != null) body.put("public_key", publicKey);
        if (flags != null) body.put("flags", flags);

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
        return getTransferAuthCode(domainName, null, null);
    }

    /**
     * Contrato: GET /transfer_auth_code com new_code (opt) e unlock_domain_for_transfer (opt).
     */
    public Future<String> getTransferAuthCode(String domainName, @Nullable Boolean newCode,
                                              @Nullable Boolean unlockDomainForTransfer) {
        List<String> params = new ArrayList<>();
        if (newCode != null) params.add("new_code=" + newCode);
        if (unlockDomainForTransfer != null) params.add("unlock_domain_for_transfer=" + unlockDomainForTransfer);
        String path = getPath(domainName + "/transfer_auth_code") + (params.isEmpty() ? "" : "?" + String.join("&", params));
        return requester.get(path)
                .thenApply(response -> response.asJSON().getString("auth_code"));
    }

    /**
     * Contrato: POST /domains/{domain}/push com receiver_push_username (req) e receiver_email (opt).
     */
    public Future<Void> push(String domainName, String receiverPushUsername) {
        return push(domainName, receiverPushUsername, null);
    }

    public Future<Void> push(String domainName, String receiverPushUsername, @Nullable String receiverEmail) {
        JSONObject body = new JSONObject();
        body.put("receiver_push_username", receiverPushUsername);
        if (receiverEmail != null) body.put("receiver_email", receiverEmail);
        return requester.post(getPath(domainName + "/push"), body.toString())
                .thenApply(response -> null);
    }

    /**
     * Contrato: POST /domains/{domain}/accept_push com push_action (req: accept/decline).
     */
    public Future<Void> acceptPush(String domainName) {
        JSONObject body = new JSONObject();
        body.put("push_action", "accept");
        return requester.post(getPath(domainName + "/accept_push"), body.toString())
                .thenApply(response -> null);
    }

    public Future<Void> declinePush(String domainName) {
        JSONObject body = new JSONObject();
        body.put("push_action", "decline");
        return requester.post(getPath(domainName + "/accept_push"), body.toString())
                .thenApply(response -> null);
    }

    /**
     * @deprecated Use {@link #getPendingPushAcceptRequests()}.
     */
    @Deprecated
    public Future<List<String>> getPendingPushRequests() {
        return getPendingPushAcceptRequests();
    }

    /**
     * Contrato: GET /domains/pending_accept_pushes — resposta em {@code data.domain_name_list}.
     */
    public Future<List<String>> getPendingPushAcceptRequests() {
        return requester.get(getPath("pending_accept_pushes"))
                .thenApply(response -> {
                    JSONObject data = response.asJSON();
                    String key = data.has("domain_name_list") ? "domain_name_list"
                            : data.has("push_domain_name") ? "push_domain_name" : "domain_name_list";
                    if (!data.has(key)) return new ArrayList<String>();
                    return data.getJSONArray(key)
                            .toList().stream()
                            .map(obj -> (String) obj)
                            .collect(Collectors.toCollection(ArrayList::new));
                });
    }

    /**
     * @deprecated O contrato correto é {@code POST /orders/{order_id}/authorize_transfer_away}
     * (ver {@link OrderClient#authorizeTransferAway}). Mantido por compatibilidade,
     * agora apontando para o endpoint correto de pedidos.
     */
    @Deprecated
    public Future<Void> authorizeTransferAway(String domainName, String orderId, boolean approve) {
        JSONObject body = new JSONObject();
        body.put("domain_name", domainName);
        body.put("approve", approve);
        return requester.post("restful/v2/orders/" + orderId + "/authorize_transfer_away", body.toString())
                .thenApply(response -> null);
    }

    /**
     * @deprecated Não consta no contrato v2 atual (sem endpoint documentado). Mantido por compatibilidade.
     */
    @Deprecated
    public Future<ResellerHoldStatus> getResellerHoldStatus(String domainName) {
        return requester.get(getPath(domainName + "/reseller/hold/status"))
                .thenApply(response -> {
                    ResellerHoldStatusResponse r = response.asClazz(gson, ResellerHoldStatusResponse.class);
                    return r.getStatus();
                });
    }

    /**
     * @deprecated Não consta no contrato v2 atual (sem endpoint documentado). Mantido por compatibilidade.
     */
    @Deprecated
    public Future<Void> setResellerHoldStatus(String domainName, ResellerHoldStatus status) {
        JSONObject body = new JSONObject();
        body.put("hold", status.getLabel());
        return requester.put(getPath(domainName + "/reseller/hold/status"), body.toString())
                .thenApply(response -> null);
    }

    /**
     * @deprecated Não consta no contrato v2 atual (sem endpoint documentado). Mantido por compatibilidade.
     */
    @Deprecated
    public Future<Long> getResellerCustomerId(String domainName) {
        return requester.get(getPath(domainName + "/reseller/customer-id"))
                .thenApply(response -> {
                    ResellerCustomerIdResponse r = response.asClazz(gson, ResellerCustomerIdResponse.class);
                    return r.getCustomerId();
                });
    }

    /**
     * @deprecated Não consta no contrato v2 atual (sem endpoint documentado). Mantido por compatibilidade.
     */
    @Deprecated
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

    /**
     * Contrato: GET /domains/{domain}/power_search_new com limit (opt), cursor (opt), status (opt).
     * Resposta em {@code data.domain_result_list} + {@code data.next_cursor}.
     */
    public Future<List<BulkSearchResult>> powerSearch(String domainName, @Nullable Integer limit,
                                                      @Nullable Integer cursor, @Nullable String status) {
        StringBuilder query = new StringBuilder(domainName + "/power_search_new");
        List<String> params = new ArrayList<>();
        if (limit != null) params.add("limit=" + limit);
        if (cursor != null) params.add("cursor=" + cursor);
        if (status != null) params.add("status=" + status);
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

    /**
     * Contrato: GET /domains com sort/page/page_size/status (opt).
     * Resposta em {@code data.domain_info_list} (+ {@code data.pagination_result}).
     */
    public Future<List<DomainInfo>> getDomainList(int page, int pageSize) {
        return getDomainList(null, null, page, pageSize);
    }

    public Future<List<DomainInfo>> getDomainList(@Nullable String sort, @Nullable String status, int page, int pageSize) {
        List<String> params = new ArrayList<>();
        if (sort != null) params.add("sort=" + sort);
        if (status != null) params.add("status=" + status);
        if (page > 0) params.add("page=" + page);
        if (pageSize > 0) params.add("page_size=" + pageSize);
        String path = "restful/v2/domains" + (params.isEmpty() ? "" : "?" + String.join("&", params));
        return requester.get(path)
                .thenApply(response -> {
                    JSONObject data = response.asJSON();
                    String key = data.has("domain_info_list") ? "domain_info_list"
                            : data.has("domain_list") ? "domain_list" : "domain_info_list";
                    if (!data.has(key)) return new ArrayList<>();
                    return data.getJSONArray(key).toList().stream()
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
     * Recupera os preços de TLDs.
     * Contrato: GET /domains/get_tld_price com currency (req), tlds (opt, lista),
     * show_multi_year (opt), sort (opt), page/page_size (opt).
     */
    public Future<TldPriceList> getTldPrice(@NonNull String currency, @Nullable List<String> tlds,
                                            @Nullable Boolean showMultiYear, @Nullable String sort,
                                            int page, int pageSize) {
        List<String> params = new ArrayList<>();
        params.add("currency=" + currency);
        params.add("page=" + page);
        params.add("page_size=" + pageSize);
        if (tlds != null && !tlds.isEmpty()) params.add("tlds=" + String.join(",", tlds));
        if (showMultiYear != null) params.add("show_multi_year=" + showMultiYear);
        if (sort != null) params.add("sort=" + sort);
        return requester.get(getPath("get_tld_price?" + String.join("&", params)))
                .thenApply(response -> response.asClazz(gson, TldPriceList.class));
    }

    /**
     * @deprecated O contrato atual usa {@code currency} (obrigatória) + {@code tlds} (lista) e não
     * possui {@code price_level} como parâmetro de requisição. Use
     * {@link #getTldPrice(String, List, Boolean, String, int, int)}.
     */
    @Deprecated
    public Future<TldPriceList> getTldPrice(@Nullable String tld, @Nullable String currency, @Nullable String priceLevel,
                                            @Nullable String sort, int page, int pageSize) {
        List<String> tlds = tld != null ? List.of(tld) : null;
        return getTldPrice(currency != null ? currency : "USD", tlds, null, sort, page, pageSize);
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

    /**
     * Contrato: PUT /records com dns_main_list/dns_sub_list/ttl/add_dns_to_current_setting (opt).
     */
    public Future<Void> setDns(String domainName, @Nullable List<JSONObject> dnsMainList,
                               @Nullable List<JSONObject> dnsSubList, @Nullable Long ttl,
                               @Nullable Boolean addDnsToCurrentSetting) {
        JSONObject body = new JSONObject();
        if (dnsMainList != null) body.put("dns_main_list", dnsMainList);
        if (dnsSubList != null) body.put("dns_sub_list", dnsSubList);
        if (ttl != null) body.put("ttl", ttl);
        if (addDnsToCurrentSetting != null) body.put("add_dns_to_current_setting", addDnsToCurrentSetting);
        return requester.post(getPath(domainName + "/records"), body.toString())
                .thenApply(response -> null);
    }

    public Future<JSONObject> getDns(String domainName) {
        return requester.get(getPath(domainName + "/records"))
                .thenApply(DynadotHttpResponse::asJSON);
    }

    public Future<Void> removeDns(String domainName, @Nullable List<JSONObject> dnsMainList,
                                  @Nullable List<JSONObject> dnsSubList) {
        JSONObject body = new JSONObject();
        if (dnsMainList != null) body.put("dns_main_list", dnsMainList);
        if (dnsSubList != null) body.put("dns_sub_list", dnsSubList);
        return requester.del(getPath(domainName + "/records"))
                .thenApply(response -> null);
    }

    /**
     * Contrato: PUT /notes com note (req).
     */
    public Future<Void> setNote(String domainName, String note) {
        JSONObject body = new JSONObject();
        body.put("note", note);
        return requester.put(getPath(domainName + "/notes"), body.toString())
                .thenApply(response -> null);
    }

    /**
     * Contrato: POST /cnnic_privacy com display_email (req) e duration (req).
     */
    public Future<Void> createCnnicPrivacy(String domainName, String displayEmail, int duration) {
        JSONObject body = new JSONObject();
        body.put("display_email", displayEmail);
        body.put("duration", duration);
        return requester.post(getPath(domainName + "/cnnic_privacy"), body.toString())
                .thenApply(response -> null);
    }

    /**
     * Contrato: GET /domains/cnnic_privacy com key_word/page/page_size (opt).
     */
    public Future<JSONObject> listCnnicPrivacy(@Nullable String keyWord, int page, int pageSize) {
        List<String> params = new ArrayList<>();
        if (keyWord != null) params.add("key_word=" + keyWord);
        if (page > 0) params.add("page=" + page);
        if (pageSize > 0) params.add("page_size=" + pageSize);
        String path = getPath("cnnic_privacy") + (params.isEmpty() ? "" : "?" + String.join("&", params));
        return requester.get(path).thenApply(DynadotHttpResponse::asJSON);
    }

    /**
     * Contrato: PUT /{domain}/cnnic_privacy com display_email (req).
     */
    public Future<Void> setCnnicPrivacy(String domainName, String displayEmail) {
        JSONObject body = new JSONObject();
        body.put("display_email", displayEmail);
        return requester.put(getPath(domainName + "/cnnic_privacy"), body.toString())
                .thenApply(response -> null);
    }

    /**
     * Contrato: DELETE /{domain}/cnnic_privacy.
     */
    public Future<Void> removeCnnicPrivacy(String domainName) {
        return requester.del(getPath(domainName + "/cnnic_privacy"))
                .thenApply(response -> null);
    }

    private String getPath(String additional) {
        return "restful/v2/domains" + (additional != null ? "/" + additional : "");
    }
}
