package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DomainStatusChangedData {

    private String domain;
    @SerializedName("change_type")
    private String changeType;
    private long expiration;
    private String status;
}