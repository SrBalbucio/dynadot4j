package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NameServerInfo {
    @SerializedName(value = "host", alternate = {"server_name"})
    private String host;
    @SerializedName("ns_name")
    private String nsName;
    @SerializedName("ip_list")
    private List<String> ipList;

    public String getServerName() {
        return host != null ? host : nsName;
    }
}
