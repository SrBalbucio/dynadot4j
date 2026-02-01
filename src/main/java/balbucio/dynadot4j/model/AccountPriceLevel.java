package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountPriceLevel {

    REGULAR(60, 5),
    BULK(600, 10),
    SUPERBULK(6000, 20);

    private final int maxRequestPerSec;
    private final int searchLimit;
}
