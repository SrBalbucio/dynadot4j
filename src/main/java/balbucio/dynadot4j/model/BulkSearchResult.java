package balbucio.dynadot4j.model;

import balbucio.dynadot4j.utils.DynadotConvertUtils;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BulkSearchResult {

    @SerializedName("domain_name")
    private String domainName;
    private String available;

    public boolean isAvailable() {
        return DynadotConvertUtils.asBool(available);
    }
}
