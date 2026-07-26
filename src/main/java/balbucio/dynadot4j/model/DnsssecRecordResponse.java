package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

@Getter
public class DnsssecRecordResponse {
    @SerializedName("dnssec_list")
    private List<DnsssecRecord> dnssecList;
}
