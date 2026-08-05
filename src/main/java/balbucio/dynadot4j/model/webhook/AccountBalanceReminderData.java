package balbucio.dynadot4j.model.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

import java.util.List;

@Getter
@ToString
public class AccountBalanceReminderData {

    @SerializedName("balance_list")
    private List<JSONObject> balanceList;
}