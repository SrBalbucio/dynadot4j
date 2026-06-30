package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DNSSECAlgorithmTest {

    @Test
    void getLabelShouldReturnLowerCase() {
        assertEquals("rsasha256", DNSSECAlgorithm.RSASHA256.getLabel());
        assertEquals("ecdsap256sha256", DNSSECAlgorithm.ECDSAP256SHA256.getLabel());
        assertEquals("ed25519", DNSSECAlgorithm.ED25519.getLabel());
    }

    @Test
    void getByNumberShouldReturnCorrectAlgorithm() {
        assertEquals(DNSSECAlgorithm.RSAMD5, DNSSECAlgorithm.getByNumber(1));
        assertEquals(DNSSECAlgorithm.RSASHA1, DNSSECAlgorithm.getByNumber(5));
        assertEquals(DNSSECAlgorithm.RSASHA256, DNSSECAlgorithm.getByNumber(8));
        assertEquals(DNSSECAlgorithm.ECDSAP256SHA256, DNSSECAlgorithm.getByNumber(13));
        assertEquals(DNSSECAlgorithm.ED25519, DNSSECAlgorithm.getByNumber(15));
        assertEquals(DNSSECAlgorithm.ECC_GOST12, DNSSECAlgorithm.getByNumber(23));
    }

    @Test
    void getByNumberShouldReturnNullForUnknown() {
        assertNull(DNSSECAlgorithm.getByNumber(99));
    }

    @Test
    void allAlgorithmsShouldHaveCorrectNumbers() {
        assertEquals(1, DNSSECAlgorithm.RSAMD5.getNumber());
        assertEquals(13, DNSSECAlgorithm.ECDSAP256SHA256.getNumber());
        assertEquals(17, DNSSECAlgorithm.SM2SM3.getNumber());
    }
}
