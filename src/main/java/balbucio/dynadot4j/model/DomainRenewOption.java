package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DomainRenewOption {

    RESET("reset"), AUTO("auto"), DONOT("donot");

    private final String label;

    public static DomainRenewOption fromLabel(String label) {
        return Arrays.stream(values())
                .filter((value) -> value.getLabel().equalsIgnoreCase(label))
                .findFirst().orElse(null);
    }

    public static DomainRenewOption fromInfo(String value) {
        return switch (value){
            case "auto-renew" -> AUTO;
            default -> DONOT;
        };
    }
}
