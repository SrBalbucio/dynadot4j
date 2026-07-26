package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class DomainAppraisalResponse {
    @SerializedName("appraisal_price")
    private String appraisalPrice;
}
