package balbucio.dynadot4j;

import balbucio.dynadot4j.client.ContactClient;
import balbucio.dynadot4j.client.DomainClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

@Getter
public class Dynadot {

    private final DynadotConfig config;
    private final DynadotRequester requester;
    private final Gson gson;

    private final DomainClient domainClient;
    private final ContactClient contactClient;

    public Dynadot(DynadotConfig config) {
        this.config = config;

        if(config.getApiKey() == null || config.getApiSecret() == null)
            throw new NullPointerException("The credentials were not entered correctly.");

        this.requester = new DynadotRequester(this);
        this.gson = new GsonBuilder()
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .create();

        this.domainClient = new DomainClient(this);
        this.contactClient = new ContactClient(this);
    }
}
