package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PendingPushRequest {

    @SerializedName("push_domain_name")
    private String pushDomainName;
}
