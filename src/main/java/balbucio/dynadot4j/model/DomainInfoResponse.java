package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DomainInfoResponse {

    @SerializedName("domain_info")
    private DomainInfo domainInfo;
}
