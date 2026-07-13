package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class TransferStatusResponse {

    @SerializedName("transfer_list")
    private List<TransferStatus> transferList;
}
