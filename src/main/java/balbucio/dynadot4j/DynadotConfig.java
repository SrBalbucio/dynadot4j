package balbucio.dynadot4j;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DynadotConfig {

    private String apiKey;
    private String apiSecret;
    private String endpointUrl;

    public static DynadotConfigBuilder createDefault() {
        return DynadotConfig.builder()
                .endpointUrl("https://api.dynadot.com");
    }

}
