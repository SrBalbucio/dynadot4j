package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

import java.util.List;

@Getter
@ToString
public class WhoisVerificationNotificationData {

    @SerializedName("contact_id")
    private int contactId;
    @SerializedName("verification_message")
    private String verificationMessage;
    @SerializedName("domain_list")
    private List<JSONObject> domainList;
}