package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ResellerHoldStatus {

    ON("on"),
    OFF("off");

    private final String label;

    public static ResellerHoldStatus fromLabel(String label) {
        return Arrays.stream(values())
                .filter((value) -> value.getLabel().equalsIgnoreCase(label))
                .findFirst().orElse(null);
    }

    public static ResellerHoldStatus fromInfo(String label) {
        return switch (label.toLowerCase()) {
            case "yes", "on", "true", "1" -> ON;
            default -> OFF;
        };
    }
}
