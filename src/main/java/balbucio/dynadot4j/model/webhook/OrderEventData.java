package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

import java.util.List;

@Getter
@ToString
public class OrderEventData {

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
    private List<JSONObject> orderItemList;
}