package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DomainPrivacy {

    FULL("full"),
    PARTIAL("partial");

    private final String label;

    public static DomainPrivacy fromLabel(String label) {
        return Arrays.stream(values())
                .filter((value) -> value.getLabel().equalsIgnoreCase(label))
                .findFirst().orElse(null);
    }

    public static DomainPrivacy fromInfo(String label){
        return switch (label){
            case "Full Privacy" -> FULL;
            default -> PARTIAL;
        };
    }
}
