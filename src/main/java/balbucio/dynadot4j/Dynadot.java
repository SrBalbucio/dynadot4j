package balbucio.dynadot4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

@Getter
public class Dynadot {

    private final DynadotConfig config;
    private final DynadotRequester requester;
    private final Gson gson;

    public Dynadot(DynadotConfig config) {
        this.config = config;
        this.requester = new DynadotRequester(this);
        this.gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .create();
    }
}
