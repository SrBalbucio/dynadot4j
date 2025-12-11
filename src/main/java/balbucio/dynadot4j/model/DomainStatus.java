package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DomainStatus {

    ACTIVE("active");

    private final String label;

    public static DomainStatus fromLabel(String label) {
        return Arrays.stream(values())
                .filter((value) -> value.getLabel().equals(label))
                .findFirst().orElse(null);
    }
}
