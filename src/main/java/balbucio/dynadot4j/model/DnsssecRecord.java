package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DnsssecRecord {
    @SerializedName("key_tag")
    private int keyTag;
    @SerializedName("digest_type")
    private String digestType;
    private String digest;
    private String algorithm;
    private String flags;
    @SerializedName("public_key")
    private String publicKey;
}
