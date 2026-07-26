package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class NameServerListResponse {
    @SerializedName("name_servers")
    private List<NameServerInfo> nameServers;
}
