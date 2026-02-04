package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DigestType {

    SHA1,
    SHA256,
    GOST,
    SHA384;

    public String getLabel() {
        return name().toLowerCase();
    }
}
