package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.exception.InvalidDomainException;
import balbucio.dynadot4j.model.Order;
import balbucio.dynadot4j.model.OrderListResponse;
import lombok.NonNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Cliente da API de pedidos (Orders) da Dynadot.
 *
 * <p>Endpoints disponíveis (RESTful v2):
 * <ul>
 *     <li>{@code order_get_status} -> {@code GET /restful/v2/orders/{order_id}}</li>
 *     <li>{@code order_get_history} -> {@code GET /restful/v2/orders}</li>
 *     <li>{@code cancel_transfer} -> {@code POST /restful/v2/orders/{order_id}/cancel_transfer}</li>
 *     <li>{@code authorize_transfer_away} -> {@code POST /restful/v2/orders/{order_id}/authorize_transfer_away}</li>
 *     <li>{@code set_transfer_auth_code} -> {@code POST /restful/v2/orders/{order_id}/update_transfer_auth_code}</li>
 * </ul>
 */
public class OrderClient extends Client {

    public OrderClient(Dynadot dynadot) {
        super(dynadot);
    }

    /**
     * Recupera os detalhes de um pedido específico.
     *
     * @param orderId id do pedido
     * @return pedido no estado atual numa promessa
     */
    public Future<Order> getOrderStatus(@NonNull String orderId) {
        if (orderId.isEmpty()) throw new InvalidDomainException(orderId);

        return requester.get(getPath(orderId))
                .thenApply(response -> {
                    OrderListResponse data = response.asClazz(gson, OrderListResponse.class);
                    List<Order> orders = data.getOrders();
                    return orders.isEmpty() ? null : orders.get(0);
                });
    }

    /**
     * Recupera a lista de pedidos da conta.
     *
     * @param page     página dos resultados
     * @param pageSize quantidade de pedidos por página
     * @return lista de pedidos numa promessa
     */
    public Future<List<Order>> getOrderHistory(int page, int pageSize) {
        List<String> params = new ArrayList<>();
        params.add("page=" + page);
        params.add("page_size=" + pageSize);

        return requester.get(getPath("?" + String.join("&", params)))
                .thenApply(response -> response.asJSON()
                        .optJSONArray("order_list") == null ? new ArrayList<>() : response.asJSON()
                        .getJSONArray("order_list").toList().stream()
                        .map(obj -> {
                            try {
                                return gson.fromJson(new JSONObject((Map<String, Object>) obj).toString(), Order.class);
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(ArrayList::new)));
    }

    /**
     * Cancela uma transferência de entrada pendente associada a um pedido.
     *
     * @param orderId    id do pedido
     * @param domainName domínio cuja transferência será cancelada
     * @return promessa de conclusão
     */
    public Future<Void> cancelTransfer(@NonNull String orderId, @NonNull String domainName) {
        if (orderId.isEmpty()) throw new InvalidDomainException(orderId);
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);

        JSONObject body = new JSONObject();
        body.put("domain_name", domainName);

        return requester.post(getPath(orderId + "/cancel_transfer"), body.toString())
                .thenApply(response -> null);
    }

    /**
     * Autoriza ou nega uma transferência de saída de um domínio.
     *
     * @param orderId    id do pedido
     * @param domainName domínio que será transferido para fora
     * @param approve    {@code true} para aprovar, {@code false} para negar
     * @return promessa de conclusão
     */
    public Future<Void> authorizeTransferAway(@NonNull String orderId, @NonNull String domainName, boolean approve) {
        if (orderId.isEmpty()) throw new InvalidDomainException(orderId);
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);

        JSONObject body = new JSONObject();
        body.put("domain_name", domainName);
        body.put("approve", approve);

        return requester.post(getPath(orderId + "/authorize_transfer_away"), body.toString())
                .thenApply(response -> null);
    }

    /**
     * Define/atualiza o código de autorização (EPP) de uma transferência de entrada.
     *
     * @param orderId    id do pedido
     * @param domainName domínio da transferência
     * @param authCode   código de autorização da transferência
     * @return promessa de conclusão
     */
    public Future<Void> setTransferAuthCode(@NonNull String orderId, @NonNull String domainName, @NonNull String authCode) {
        if (orderId.isEmpty()) throw new InvalidDomainException(orderId);
        if (domainName.isEmpty()) throw new InvalidDomainException(domainName);
        if (authCode.isEmpty()) throw new InvalidDomainException(authCode);

        JSONObject body = new JSONObject();
        body.put("domain_name", domainName);
        body.put("auth_code", authCode);

        return requester.post(getPath(orderId + "/update_transfer_auth_code"), body.toString())
                .thenApply(response -> null);
    }

    private String getPath(String additional) {
        return "restful/v2/orders" + (additional != null ? "/" + additional : "");
    }
}