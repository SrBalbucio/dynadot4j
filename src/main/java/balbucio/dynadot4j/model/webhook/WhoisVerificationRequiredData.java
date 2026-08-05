package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

import java.util.List;

@Getter
@ToString
public class WhoisVerificationRequiredData {

    @SerializedName("whois_name")
    private String whoisName;
    @SerializedName("contact_id")
    private int contactId;
    @SerializedName("verify_link")
    private String verifyLink;
    @SerializedName("verify_end_time")
    private String verifyEndTime;
    @SerializedName("domain_list")
    private List<JSONObject> domainList;
}