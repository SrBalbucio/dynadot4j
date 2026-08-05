package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DomainSuspensionStatusChangedData {

    private String domain;
    private boolean suspended;
    @SerializedName("suspension_type")
    private String suspensionType;
    private String reason;
    private String message;
    @SerializedName("status_changed_timestamp")
    private long statusChangedTimestamp;
}