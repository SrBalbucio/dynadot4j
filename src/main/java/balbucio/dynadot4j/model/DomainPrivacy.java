package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DomainPrivacy {

    FULL("full"),
    PARTIAL("partial");

    private final String label;
}
