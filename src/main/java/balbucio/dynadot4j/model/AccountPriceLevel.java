package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountPriceLevel {

    REGULAR(1, 1),
    BULK(10, 1),
    SUPERBULK(100, 1);

    private final int maxRequestPerSec;
    private final int delay;
}
