package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountPriceLevel {

    REGULAR(1, 5),
    BULK(60, 10),
    SUPERBULK(600, 20);

    private final int maxRequestPerSec;
    private final int searchLimit;
}
