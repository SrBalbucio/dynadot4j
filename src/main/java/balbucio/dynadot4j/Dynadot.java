package balbucio.dynadot4j;

import lombok.Getter;

@Getter
public class Dynadot {

    private final DynadotConfig config;

    public Dynadot(DynadotConfig config) {
        this.config = config;
    }
}
