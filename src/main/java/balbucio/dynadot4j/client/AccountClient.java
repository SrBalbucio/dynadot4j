package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.model.DynadotAccountInfo;
import org.json.JSONObject;

import java.util.concurrent.Future;

public class AccountClient extends Client {

    public AccountClient(Dynadot dynadot) {
        super(dynadot);
    }

    public Future<DynadotAccountInfo> getAccountInfo() {
        return requester.get(getPath("info"))
                .thenApply((response) -> {
                    JSONObject data = response.asJSON();
                    return gson.fromJson(data.getJSONObject("account_info").toString(), DynadotAccountInfo.class);
                });
    }

    private String getPath(String additional) {
        return "restful/v2/accounts" + (additional != null ? "/" + additional : "");
    }
}
