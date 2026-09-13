package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TransferStatus {

    @SerializedName("order_id")
    private String orderId;

    @SerializedName("transfer_status")
    private String transferStatus;

    @SerializedName("expiration_date")
    private Long expirationDate;

    @SerializedName("order_created_date")
    private Long orderCreatedDate;

    @SerializedName("order_completed_date")
    private Long orderCompletedDate;
}
