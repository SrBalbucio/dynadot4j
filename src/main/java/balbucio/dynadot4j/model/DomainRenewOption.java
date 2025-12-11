package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DomainRenewOption {

    RESET("reset"), AUTO("auto"), DONOT("donot");

    private final String label;
}
