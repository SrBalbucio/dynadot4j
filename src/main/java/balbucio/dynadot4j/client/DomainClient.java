package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.action.DomainRegistration;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.*;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public Future<List<DomainSearchResult>> searchBulk(@NonNull String domainName, @Nullable String currency) {
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
    public Future<List<DomainSearchResult>> searchBulk(@NonNull List<String> domainNames, @Nullable String currency) {
        if (domainNames.isEmpty())
            throw new InvalidDomainException(domainNames);
        if (currency == null)
            currency = "USD";
        if (domainNames.size() > config.getPriceLevel().getSearchLimit())
            domainNames = domainNames.subList(0, config.getPriceLevel().getSearchLimit() - 1);

        CompletableFuture<DynadotHttpResponse> future =
                requester.get(getPath("bulk_search?show_price=true&currency=" + currency.toUpperCase() + "&domain_name_list=" + String.join(",", domainNames)));

        return future.thenApply((response) -> {
            return response.asJSON()
                    .getJSONArray("domain_result_list").toList().stream()
                    .map((obj) -> gson.fromJson(obj.toString(), DomainSearchResult.class))
                    .collect(Collectors.toCollection(ArrayList::new));
        });
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
     * @param algorithm algoritmo
     * @param digest digest
     * @param digestType tipo do digest
     * @param keyTag key tag
     * @param publicKey public key
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
                    JSONObject data = response.asJSON();
                    JSONArray domainList = data.getJSONArray("domainInfo");
                    return gson.fromJson(domainList.getJSONObject(0).toString(), DomainInfo.class);
                });
    }

    private String getPath(String additional) {
        return "restful/v2/domains" + (additional != null ? "/" + additional : "");
    }
}
