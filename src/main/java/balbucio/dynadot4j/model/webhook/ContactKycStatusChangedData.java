package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ContactKycStatusChangedData {

    @SerializedName("contact_id")
    private int contactId;
    private String status;
}
