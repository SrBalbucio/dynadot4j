package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class DomainRestoreResult {
    @SerializedName("order_id")
    private long orderId;
}
