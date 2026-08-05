package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Representa um pedido (order) retornado pela API de Orders da Dynadot.
 */
@Getter
@ToString
public class Order {

    @SerializedName("order_id")
    private int orderId;
    @SerializedName("submitted_date")
    private long submittedDate;
    private String currency;
    @SerializedName("total_cost")
    private String totalCost;
    @SerializedName("total_paid")
    private String totalPaid;
    @SerializedName("payment_method")
    private String paymentMethod;
    private String status;
    @SerializedName("order_item_list")
    private List<OrderItem> orderItemList;

    public Date getSubmittedDate() {
        return submittedDate > 0 ? new Date(submittedDate) : null;
    }

    public List<OrderItem> getOrderItems() {
        return orderItemList != null ? orderItemList : new ArrayList<>();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class OrderItem {
        private String type;
        private String name;
        private String duration;
        private String cost;
        private String status;
    }
}