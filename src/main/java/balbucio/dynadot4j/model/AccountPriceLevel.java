package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountPriceLevel {

    REGULAR(1, 3),
    BULK(10, 3),
    SUPERBULK(100, 3);

    private final int maxRequestPerSec;
    private final int delay;
}
