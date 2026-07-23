package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class ResellerHoldStatusResponse {

    @SerializedName("hold")
    private String hold;

    public ResellerHoldStatus getStatus() {
        return ResellerHoldStatus.fromInfo(hold);
    }
}
