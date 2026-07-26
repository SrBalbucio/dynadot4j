package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NameServerInfo {
    private String host;
    @SerializedName("ns_name")
    private String nsName;
}
