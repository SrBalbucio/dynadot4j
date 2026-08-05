package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DomainTransferAwayData {

    private String domain;
    @SerializedName("gaining_registrar")
    private String gainingRegistrar;
    @SerializedName("order_id")
    private String orderId;
}