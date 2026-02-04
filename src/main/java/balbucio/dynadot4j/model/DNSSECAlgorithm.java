package balbucio.dynadot4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DNSSECAlgorithm {

    RSAMD5(1),
    DH(2),
    DSA(3),
    RSASHA1(5),
    DSA_NSEC3_SHA1(6),
    RSASHA1_NSEC3_SHA1(7),
    RSASHA256(8),
    RSASHA512(10),
    ECC_GOST(12),
    ECDSAP256SHA256(13),
    ECDSAP384SHA384(14),
    ED25519(15),
    ED448(16),
    SM2SM3(17),
    ECC_GOST12(23);

    private final int number;

    public String getLabel() {
        return name().toLowerCase();
    }

    public static DNSSECAlgorithm getByNumber(int number) {
        return Arrays.stream(values()).filter((alg) -> alg.number == number).findFirst().orElse(null);
    }
}
