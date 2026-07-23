package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class ResellerCustomerIdResponse {

    @SerializedName("customer_id")
    private long customerId;
}
