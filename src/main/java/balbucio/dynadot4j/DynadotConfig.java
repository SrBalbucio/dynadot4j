package balbucio.dynadot4j;

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
    private ScheduledExecutorService executorService;

    public static DynadotConfigBuilder createDefault() {
        return DynadotConfig.builder()
                .executorService(Executors.newSingleThreadScheduledExecutor())
                .endpointUrl("https://api.dynadot.com");
    }

}
