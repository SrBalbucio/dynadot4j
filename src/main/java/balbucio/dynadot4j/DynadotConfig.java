package balbucio.dynadot4j;

import balbucio.dynadot4j.model.AccountPriceLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
@Setter
@Builder
public class DynadotConfig {

    private String apiKey;
    private String apiSecret;
    private String endpointUrl;
    private AccountPriceLevel priceLevel;
    private ScheduledExecutorService executorService;
    private int requestThreads = 0;
    private boolean debug;

    public static DynadotConfigBuilder createDefault() {
        return DynadotConfig.builder()
                .debug(false)
                .priceLevel(AccountPriceLevel.REGULAR)
                .executorService(Executors.newSingleThreadScheduledExecutor())
                .endpointUrl("https://api.dynadot.com")
                .requestThreads(0);
    }

}
