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
}
