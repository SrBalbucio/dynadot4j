package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.DynadotRequester;
import com.google.gson.Gson;
import lombok.Getter;

@Getter
public abstract class Client {

    protected Dynadot dynadot;
    protected DynadotConfig config;
    protected DynadotRequester requester;
    protected Gson gson;

    public Client(Dynadot dynadot) {
        this.dynadot = dynadot;
        this.config = dynadot.getConfig();
        this.requester = dynadot.getRequester();
        this.gson = dynadot.getGson();
    }
}
