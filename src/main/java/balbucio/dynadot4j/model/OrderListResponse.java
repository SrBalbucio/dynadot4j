package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Envelope que contém a lista de pedidos retornada pelos comandos
 * {@code order_get_status} e {@code order_get_history}.
 */
@Getter
@ToString
public class OrderListResponse {

    @SerializedName("order_list")
    private List<Order> orderList;

    public List<Order> getOrders() {
        return orderList != null ? orderList : new ArrayList<>();
    }
}